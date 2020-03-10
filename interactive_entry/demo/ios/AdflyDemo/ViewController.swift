//
//  ViewController.swift
//  AdflyDemo
//
//  Created by 全尼古拉斯 on 2020/3/9.
//  Copyright © 2020 全尼古拉斯. All rights reserved.
//

import UIKit
import Alamofire
import ObjectMapper
import SDWebImage
import GZIP
import SwiftyJSON
import CryptoSwift
import AdSupport

class AdModel: Mappable {
    var imageUrl : String?
    var landingUrl : String?
    var reportExposeUrl : String?
    var reportClickUrl : String?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        imageUrl <- map["imageUrl"]
        landingUrl <- map["landingUrl"]
        reportExposeUrl <- map["reportExposeUrl"]
        reportClickUrl <- map["reportClickUrl"]
    }
}

let appKey = "demo"
let appSecret = "ac4a10da9e7f62adb59dbe7f62adb59dbe770e8d"

class ViewController: UIViewController {

    @IBOutlet weak var adImg: UIImageView!
    @IBOutlet weak var requestAdBtn: UIButton!
    @IBOutlet weak var logTextView: UITextView!
    @IBOutlet weak var cleanLogBtn: UIButton!
    
    var adModel: AdModel? {
        didSet {
            setAd()
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        let tap = UITapGestureRecognizer.init(target: self, action: #selector(onClickAdAction))
        adImg.addGestureRecognizer(tap)
        adImg.isUserInteractionEnabled = false
        
        MapLocationManager.shared.startLocation()
    }
    
    @IBAction func requestAdAction(_ sender: Any) {
        self.setLogText(log: "request Ad start......")
        JSONRequest<AdModel>()
            .post(url: getURL(url: "https://api.adfly.vn/api/ig/entry/query"))
            .go(keys: [], onSuccessed: {[weak self] (adModel) in
                self?.adModel = adModel
                self?.setLogText(log: "request Ad success:\n imageUrl:  \(self?.adModel?.imageUrl ?? "")\n landingUrl:  \(self?.adModel?.landingUrl ?? "")\n reportExposeUrl:  \(self?.adModel?.reportExposeUrl ?? "")\n reportClickUrl:  \(self?.adModel?.reportClickUrl ?? "")")
            }, onFailed: { (errorCode, errorStr) in
                self.setLogText(log: "request Ad failed: code:\(errorCode) message:\(errorStr)")
            }, onError: {
                self.setLogText(log: "request Ad error")
            })
    }
    
    
    @IBAction func cleanLogAction(_ sender: Any) {
        logTextView.text = ""
    }
    
    @objc func onClickAdAction() {
        if let url = URL.init(string: adModel?.landingUrl ?? "") {
            let vc = UIStoryboard.init(name: "LandPageController", bundle: nil).instantiateViewController(withIdentifier: "LandPageController") as! LandPageController
            vc.url = url
            self.navigationController?.pushViewController(vc, animated: true)
            TrackerManager.shared.logTracker(urls: [(adModel?.reportClickUrl ?? "")])
        }
    }
    
    func setAd() {
        adImg.sd_setImage(with: URL.init(string: adModel?.imageUrl ?? ""), completed: nil)
        adImg.isUserInteractionEnabled = true
        TrackerManager.shared.logTracker(urls: [(adModel?.reportExposeUrl ?? "")])
    }
    
    
    func setLogText(log: String) {
        let oldLog = logTextView.text ?? ""
        logTextView.text = oldLog + "\n" + log
        self.logTextView.scrollRangeToVisible(NSRange.init(location: logTextView.text.count, length: 1))
    }
    
    
    func getURL(url: String) -> String {
        let generalDelimitersToEncode = ":#[]@"
        let subDelimitersToEncode = "!$&'()*+,;=/?"
        var allowedCharacterSet = CharacterSet.urlQueryAllowed
        allowedCharacterSet.remove(charactersIn: "\(generalDelimitersToEncode)\(subDelimitersToEncode)")

        let ci: String = (getCiStr().data(using: .utf8)! as NSData).gzipped()?.base64EncodedString() ?? ""
        let ciEncode: String = ci.addingPercentEncoding(withAllowedCharacters: allowedCharacterSet) ?? ""
        let timestamp = Int64(Date().timeIntervalSince1970 * 1000)
        let nonce = arc4random()
        let nonceEncode = "\(nonce)".addingPercentEncoding(withAllowedCharacters: allowedCharacterSet) ?? ""
        let deviceId = getMyIDFA().addingPercentEncoding(withAllowedCharacters: allowedCharacterSet) ?? ""
        let tempUrl: String = "\(url)?appKey=\(appKey)&ci=\(ciEncode)&timestamp=\(timestamp)&nonce=\(nonceEncode)&deviceId=\(deviceId)&signature=\(getSignStr(timestamp: timestamp, ci: ci, nonce: Int(nonce)))"
        print(tempUrl)
        return tempUrl
    }
    

    
    func getSignStr(timestamp: Int64, ci: String, nonce: Int) -> String {
        let deviceId = getMyIDFA()
        let nonce = nonce
        let str = "appKey=\(appKey),appSecret=\(appSecret),ci=\(ci),deviceId=\(deviceId),nonce=\(nonce),timestamp=\(timestamp)"
        let sha1 = str.sha1()
        return sha1
    }
    
    
    func getCiStr() -> String {
        var params: [String: String] = [:]
        params["idfa"] = getMyIDFA()
        params["longitude"] = "\(MapLocationManager.shared.longitude ?? 0.0)"
        params["latitude"] = "\(MapLocationManager.shared.latitude ?? 0.0)"
        params["os"] = "ios_\(UIDevice.current.systemVersion)"
        params["network"] = "wifi"
        params["apps"] = "com.xxb.AdflyDemo"
        return params.toJSONString() ?? ""
    }
    
    
    var myIDFA: String = ""
    func getMyIDFA() -> String {
        if myIDFA == "" {
            if ASIdentifierManager.shared().isAdvertisingTrackingEnabled {
                let idfa = ASIdentifierManager.shared().advertisingIdentifier.uuidString
                if !idfa.contains("00000000-0000-0000-0000-000000000000") {
                    myIDFA = idfa
                }
            }
        }
        return myIDFA
    }
}



extension String {
    func base64EncodeString() -> String? {
        let data = self.data(using: .utf8)
        return data?.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))
    }
}

extension Dictionary {
    func toJSONString() -> String? {
        if (JSONSerialization.isValidJSONObject(self)) {
            let data = try? JSONSerialization.data(withJSONObject: self, options: [])
            let jsonString = String(data: data ?? Data(), encoding: .utf8)
            return jsonString
        } else {
            print("JSONSerialization error")
            return nil
        }
    }
}
