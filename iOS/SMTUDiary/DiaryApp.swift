import SwiftUI

@main
struct DiaryApp: App {
    @AppStorage("selectedGroup") private var group = ""
    @State private var choosingGroup = false

    var body: some Scene {
        WindowGroup {
            NavigationStack {
                if group.isEmpty {
                    GroupPicker(selectedGroup: group, onSelect: selectGroup)
                } else {
                    ScheduleView(group: group, onChooseGroup: { choosingGroup = true })
                        .id(group)
                }
            }
            .sheet(isPresented: $choosingGroup) {
                NavigationStack {
                    GroupPicker(selectedGroup: group, onSelect: selectGroup)
                        .toolbar {
                            ToolbarItem(placement: .cancellationAction) {
                                Button("Закрыть") { choosingGroup = false }
                            }
                        }
                }
            }
            .tint(Color("AccentColor"))
        }
    }

    private func selectGroup(_ selected: String) {
        group = selected
        choosingGroup = false
    }
}

@MainActor
final class ResourceModel<Value: Decodable>: ObservableObject {
    @Published private(set) var value: Value?
    @Published private(set) var failed = false
    @Published private(set) var loading = false

    private let resource: DiaryResource<Value>

    init(_ resource: DiaryResource<Value>) {
        self.resource = resource
        value = resource.cached()
    }

    func reload() async {
        guard !loading else { return }
        loading = true
        failed = false
        defer { loading = false }
        do {
            let fresh = try await resource.fetch()
            try Task.checkCancellation()
            value = fresh
        } catch {
            if !Task.isCancelled { failed = true }
        }
    }
}

struct LoadErrorView: View {
    let title: String
    let retry: () async -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Label(title, systemImage: "wifi.exclamationmark")
                .foregroundStyle(.secondary)
            Button("Повторить") { Task { await retry() } }
                .buttonStyle(.borderless)
        }
        .padding(.vertical, 6)
    }
}

@MainActor
struct GroupPicker: View {
    let selectedGroup: String
    let onSelect: (String) -> Void

    @StateObject private var groups = ResourceModel(DiaryAPI.groups)
    @State private var query = ""

    private var filteredGroups: [String] {
        let term = query.trimmingCharacters(in: .whitespacesAndNewlines)
        return (groups.value ?? []).filter { term.isEmpty || $0.localizedStandardContains(term) }
    }

    var body: some View {
        List {
            if groups.failed {
                LoadErrorView(title: "Не удалось загрузить список групп", retry: groups.reload)
            }
            if groups.value == nil && !groups.failed {
                ProgressView("Загрузка групп…")
            } else if let values = groups.value, filteredGroups.isEmpty {
                Text(values.isEmpty ? "Список групп пуст" : "Группы не найдены")
                    .foregroundStyle(.secondary)
            }
            ForEach(filteredGroups, id: \.self) { group in
                Button {
                    onSelect(group)
                } label: {
                    HStack {
                        Text(group)
                            .foregroundStyle(.primary)
                        Spacer()
                        if selectedGroup == group {
                            Image(systemName: "checkmark")
                                .accessibilityLabel("Выбранная группа")
                        }
                    }
                    .contentShape(Rectangle())
                }
            }
        }
        .navigationTitle("Выберите группу")
        .searchable(text: $query, prompt: "Номер группы")
        .refreshable { await groups.reload() }
        .task { await groups.reload() }
    }
}
