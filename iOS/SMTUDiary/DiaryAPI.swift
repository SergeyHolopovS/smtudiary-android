import Foundation
#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

struct DiaryResource<Value: Decodable> {
    private let path: [String]

    init(path: [String]) {
        self.path = path
    }

    func cached() -> Value? {
        guard let cacheURL, let data = try? Data(contentsOf: cacheURL) else { return nil }
        return try? JSONDecoder().decode(Value.self, from: data)
    }

    func fetch() async throws -> Value {
        let url = path.reduce(URL(string: "https://smtudiary.ru/api/v1/")!) {
            $0.appendingPathComponent($1)
        }
        let request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalCacheData, timeoutInterval: 15)
        let (data, response) = try await URLSession.shared.data(for: request)
        guard let response = response as? HTTPURLResponse, (200..<300).contains(response.statusCode) else {
            throw URLError(.badServerResponse)
        }
        let value = try JSONDecoder().decode(Value.self, from: data)
        try Task.checkCancellation()
        if let cacheURL {
            try? FileManager.default.createDirectory(at: cacheURL.deletingLastPathComponent(),
                                                     withIntermediateDirectories: true)
            try? data.write(to: cacheURL, options: .atomic)
        }
        return value
    }

    private var cacheURL: URL? {
        let filename = path.joined(separator: "-") + ".json"
        return FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first?
            .appendingPathComponent("ScheduleCache", isDirectory: true)
            .appendingPathComponent(filename)
    }
}

enum DiaryAPI {
    static let groups = DiaryResource<[String]>(path: ["groups"])

    static func lessons(group: String) -> DiaryResource<[Lesson]> {
        DiaryResource(path: ["lessons", group])
    }
}
