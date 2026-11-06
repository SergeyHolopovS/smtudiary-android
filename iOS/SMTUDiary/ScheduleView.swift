import SwiftUI

@MainActor
struct ScheduleView: View {
    let group: String
    let onChooseGroup: () -> Void

    @StateObject private var lessons: ResourceModel<[Lesson]>
    @State private var selectedDate = Schedule.initialDate(today: .now)

    init(group: String, onChooseGroup: @escaping () -> Void) {
        self.group = group
        self.onChooseGroup = onChooseGroup
        _lessons = StateObject(wrappedValue: ResourceModel(DiaryAPI.lessons(group: group)))
    }

    var body: some View {
        VStack(spacing: 0) {
            calendarHeader
            lessonList
        }
        .navigationTitle("Расписание")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: onChooseGroup) {
                    Label("Сменить группу", systemImage: "gearshape")
                }
            }
        }
        .safeAreaInset(edge: .bottom) {
            nextDayButton
        }
        .task { await lessons.reload() }
    }

    private var calendarHeader: some View {
        let monday = Schedule.monday(of: selectedDate)
        return VStack(alignment: .leading, spacing: 8) {
            Text("Группа \(group) · \(Schedule.weekType(for: selectedDate).title)")
                .font(.subheadline)
                .foregroundStyle(.secondary)

            HStack {
                Text(Schedule.label(selectedDate, format: "LLLL yyyy"))
                    .font(.headline)
                    .accessibilityAddTraits(.isHeader)
                Spacer()
                weekButton("Предыдущая неделя", icon: "chevron.left", days: -7)
                weekButton("Следующая неделя", icon: "chevron.right", days: 7)
            }

            HStack(spacing: 6) {
                ForEach(0..<6) { index in
                    dayButton(Schedule.addingDays(index, to: monday))
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.bottom, 12)
    }

    private func weekButton(_ title: String, icon: String, days: Int) -> some View {
        Button {
            selectedDate = Schedule.addingDays(days, to: selectedDate)
        } label: {
            Image(systemName: icon)
                .font(.headline)
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
        .accessibilityLabel(title)
    }

    private func dayButton(_ date: Date) -> some View {
        let selected = Schedule.calendar.isDate(date, inSameDayAs: selectedDate)
        return Button {
            selectedDate = date
        } label: {
            VStack(spacing: 4) {
                Text(Schedule.label(date, format: "EEEEEE"))
                    .font(.caption)
                Text(Schedule.label(date, format: "d"))
                    .font(.headline.monospacedDigit())
            }
            .frame(maxWidth: .infinity, minHeight: 52)
            .foregroundStyle(selected ? Color.white : Color.primary)
            .background {
                RoundedRectangle(cornerRadius: 12)
                    .fill(selected ? Color("SelectedDay") : Color.secondary.opacity(0.12))
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(Schedule.label(date, format: "EEEE, d MMMM"))
        .accessibilityAddTraits(selected ? .isSelected : [])
    }

    private var lessonList: some View {
        let dayLessons = Schedule.lessons(lessons.value ?? [], on: selectedDate)
        return List {
            Section {
                if lessons.failed {
                    LoadErrorView(
                        title: lessons.value == nil
                            ? "Не удалось загрузить расписание"
                            : "Не удалось обновить. Показано сохранённое расписание",
                        retry: lessons.reload
                    )
                }
                if lessons.value != nil {
                    if dayLessons.isEmpty {
                        Text("Нет пар")
                            .foregroundStyle(.secondary)
                    }
                    ForEach(dayLessons.indices, id: \.self) { index in
                        LessonRow(lesson: dayLessons[index])
                    }
                } else if !lessons.failed {
                    ProgressView("Загрузка расписания…")
                }
            } header: {
                HStack {
                    Text(Schedule.label(selectedDate, format: "EEEE, d MMMM"))
                    Spacer()
                    if !dayLessons.isEmpty {
                        Text(Schedule.countText(dayLessons.count))
                    }
                }
                .textCase(nil)
            }
        }
        .listStyle(.insetGrouped)
        .refreshable { await lessons.reload() }
    }

    private var nextDayButton: some View {
        let today = Date.now
        let next = Schedule.nextDate(today: today)
        let tomorrow = Schedule.addingDays(1, to: today)
        let title = Schedule.calendar.isDate(next, inSameDayAs: tomorrow)
            ? "Завтра" : Schedule.label(next, format: "EEEE, d MMMM")
        let summary = lessons.value.map { Schedule.summary(Schedule.lessons($0, on: next)) }
        return Button {
            selectedDate = next
        } label: {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.headline)
                    if let summary {
                        Text(summary)
                            .font(.subheadline)
                    }
                }
                Spacer()
                Image(systemName: "arrow.right")
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .buttonStyle(.borderedProminent)
        .tint(Color("SelectedDay"))
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(.bar)
    }
}

private struct LessonRow: View {
    let lesson: Lesson

    var body: some View {
        HStack(alignment: .top, spacing: 14) {
            VStack(alignment: .leading, spacing: 4) {
                Text(lesson.startText).font(.headline)
                Text(lesson.endText).font(.caption).foregroundStyle(.secondary)
            }
            .monospacedDigit()

            VStack(alignment: .leading, spacing: 4) {
                Text(lesson.name ?? "Неизвестная пара").font(.headline)
                Text(lesson.teacher ?? "Неизвестный преподаватель")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Text(lesson.type ?? "Неизвестный тип")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                Label(lesson.room, systemImage: "mappin")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(.vertical, 5)
        .accessibilityElement(children: .combine)
    }
}
