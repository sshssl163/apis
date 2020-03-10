//
//  JsonRequest.swift
//  ConsultantLoop
//
//

import Foundation
import SwiftyJSON
import ObjectMapper
import Alamofire

enum RequestMethod{
    case Post
    case Get
}

class JSONRequest<N: Mappable> {
    
    private var method : RequestMethod = .Get
    
    private var curUrl = ""
    
    private var parameters: [String : Any] = [:]
    
    private var dataRequest : DataRequest?
    init() {
    }

    func fetch(url : String) -> JSONRequest{
        curUrl = url
        self.method = .Get
        return self
    }
    
    func post(url : String) -> JSONRequest{
        curUrl = url
        self.method = .Post
        return self
    }
    
    func paras(p : [String: Any]) -> JSONRequest{
        _ = p.reduce("") { (str, p) -> String in
            parameters[p.0] = p.1
            return ""
        }
        return self
    }
    
    
    func go(keys: [String], onSuccessed: ((N) -> Void)?, onFailed: ((Int, String) -> Void)?, onError: (() -> Void)?) {
        var httpMethod = Alamofire.HTTPMethod.get
        var headers: [String : String] = [:]
        if method == RequestMethod.Post {
            httpMethod = .post
        }
       
        dataRequest = Alamofire.request(curUrl, method: httpMethod, parameters: parameters, encoding: JSONEncoding.default, headers: headers).responseJSON { (data) in
  
            guard data.result.isSuccess, let jsonData = data.data else {
                var jsonStr: String?
                if let jsonData = data.data {
                    do{
                        jsonStr = String(data: jsonData, encoding: .ascii)
                        print(jsonStr ?? "")
                    } catch {
                        
                    }
                }
                
                DispatchQueue.main.async(execute: { () -> Void in
                    onError?()
                })
                
                return
            }

            var tempJson : JSON?
            do{
                tempJson  = try JSON(data: jsonData)
            } catch {
                
            }
            guard var json = tempJson else {
                return
            }
            guard let code = json["code"].int else {
                do{
                    if let result = try ResultParse<N>().parse(response: json, keys: keys) {
                        DispatchQueue.main.async(execute: { () -> Void in
                            onSuccessed?(result)
                        })
                        
                    } else {
                        DispatchQueue.main.async(execute: { () -> Void in
                            onFailed?(-1000, "str_connect_error_text")
                        })
                
                    }
                } catch {
                    DispatchQueue.main.async(execute: { () -> Void in
                        onFailed?(-999, "str_nokey_error_text")
                    })
                    
                }
                return
            }
           
            let message = json["message"].string ?? ""
            DispatchQueue.main.async(execute: { () -> Void in
                onFailed?(code, message)
            })
         
        }
    }
    
}

