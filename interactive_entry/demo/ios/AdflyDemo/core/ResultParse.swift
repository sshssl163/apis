//
//  ResultParse.swift
//  

import Foundation
import SwiftyJSON
import ObjectMapper
public enum ParseError: Error {
    case NilError
}
class ResultParse<N: Mappable> {
    
    func parse(response: JSON, keys: [String]) throws -> N?   {
        var json: JSON = response
        for (_, key) in keys.enumerated() {
            json = json[key]
        }
        if json == nil {
            throw ParseError.NilError
        }
        return Mapper<N>().map(JSONString: json.description)
    }
    
    func parseArray(response: JSON, keys: [String]) throws  -> [N]? {
        var json: JSON = response
        for (_, key) in keys.enumerated() {
            json = json[key]
        }
        if json == nil {
            throw ParseError.NilError
        }
        return Mapper<N>().mapArray(JSONString: json.description)
    }
    
}
