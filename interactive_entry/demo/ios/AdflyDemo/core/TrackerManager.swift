//
//  TrackerManager.swift
//  AdflyDemo
//

import UIKit
import Alamofire

//upload tracker url
class TrackerManager: NSObject {
    
    static let shared = TrackerManager.init()
    private var failUrlsDict: [String: TimeInterval] = [:]
    private var failUrls: [String] = []
    let invalidTime: Double = 3 * 60 * 60
    
    func logTracker(urls: [String]) {
        if urls.count == 0 { return }
        let group = DispatchGroup()
        for urlString in urls {
            group.enter()
            Alamofire.request(urlString).response{ (result) in
                let code = result.response?.statusCode ?? 0
                if code >= 200 && code < 300 {
                    // log success
                    self.failUrlsDict.removeValue(forKey: urlString)
                    self.retryLogFailTracker()
                    print("normalTracker: success ---log", urlString)
                } else {
                    if self.failUrlsDict[urlString] == nil {
                        self.failUrlsDict[urlString] = Date().timeIntervalSince1970
                    }
                    print("normalTracker: failed ---log", urlString)
                }
                group.leave()
            }
        }
        group.notify(queue: .main) {
            for (url, failTime) in self.failUrlsDict {
                if Date().timeIntervalSince1970 - failTime < self.invalidTime {
                    self.failUrls.append(url)
                }
            }
        }
    }
    
    func retryLogFailTracker() {
        logTracker(urls: failUrls)
        failUrls.removeAll()
    }
}
