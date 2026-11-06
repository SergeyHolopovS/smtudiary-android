import Foundation

enum WeekType: String, Codable {
    case upper = "UPPER"
    case lower = "LOWER"

    var title: String {
        self == .upper ? "Верхняя неделя" : "Нижняя неделя"
    }
}

struct Lesson: Codable, Hashable {
    let name: String?
    let start: String
    let end: String
    let teacher: String?
    let type: String?
    let week: WeekType
    let room: String
    let group: String
    let day: String

    var startText: String { start.trimmingCharacters(in: .whitespacesAndNewlines) }
    var endText: String { end.trimmingCharacters(in: .whitespacesAndNewlines) }

    var startMinutes: Int? {
        let parts = startText.split(separator: ":", omittingEmptySubsequences: false)
        guard parts.count == 2,
              (1...2).contains(parts[0].count), parts[1].count == 2,
              let hour = Int(parts[0]), (0..<24).contains(hour),
              let minute = Int(parts[1]), (0..<60).contains(minute)
        else { return nil }
        return hour * 60 + minute
    }
}

enum Schedule {
    static let locale = Locale(identifier: "ru_RU")
    static let calendar: Calendar = {
        var calendar = Calendar(identifier: .gregorian)
        calendar.locale = locale
        calendar.timeZone = TimeZone(identifier: "Europe/Moscow")!
        calendar.firstWeekday = 2
        return calendar
    }()

    private static let weekdays = [
        "Воскресенье", "Понедельник", "Вторник", "Среда",
        "Четверг", "Пятница", "Суббота"
    ]

    static func addingDays(_ days: Int, to date: Date) -> Date {
        calendar.date(byAdding: .day, value: days, to: date)!
    }

    static func monday(of date: Date) -> Date {
        let weekday = calendar.component(.weekday, from: date)
        return addingDays(-((weekday + 5) % 7), to: calendar.startOfDay(for: date))
    }

    static func initialDate(today: Date) -> Date {
        calendar.component(.weekday, from: today) == 1 ? addingDays(1, to: today) : today
    }

    static func nextDate(today: Date) -> Date {
        addingDays(calendar.component(.weekday, from: today) == 7 ? 2 : 1, to: today)
    }

    static func weekType(for date: Date) -> WeekType {
        let year = calendar.component(.year, from: date)
        let month = calendar.component(.month, from: date)
        let academicYear = month >= 9 ? year : year - 1
        let september = calendar.date(from: DateComponents(year: academicYear, month: 9, day: 1))!
        let days = calendar.dateComponents([.day], from: monday(of: september), to: monday(of: date)).day!
        return (days / 7).isMultiple(of: 2) ? .upper : .lower
    }

    static func lessons(_ lessons: [Lesson], on date: Date) -> [Lesson] {
        let weekday = weekdays[calendar.component(.weekday, from: date) - 1]
        let week = weekType(for: date)
        return lessons.filter { $0.day == weekday && $0.week == week }
            .sorted { ($0.startMinutes ?? Int.max) < ($1.startMinutes ?? Int.max) }
    }

    static func countText(_ count: Int) -> String {
        if count == 0 { return "Нет пар" }
        if (11...14).contains(count % 100) { return "\(count) пар" }
        switch count % 10 {
        case 1: return "\(count) пара"
        case 2...4: return "\(count) пары"
        default: return "\(count) пар"
        }
    }

    static func summary(_ lessons: [Lesson]) -> String {
        let count = countText(lessons.count)
        guard let first = lessons.first, first.startMinutes != nil else { return count }
        return "\(count) · начало в \(first.startText)"
    }

    static func label(_ date: Date, format: String) -> String {
        let formatter = DateFormatter()
        formatter.locale = locale
        formatter.calendar = calendar
        formatter.timeZone = calendar.timeZone
        formatter.dateFormat = format
        let label = formatter.string(from: date)
        return label.prefix(1).uppercased() + label.dropFirst()
    }
}
