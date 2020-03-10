//
//  LocationManager.swift
//  TopNews
//
//  Created by xb on 2017/12/12.
//  Copyright © 2017年 xb. All rights reserved.
//

import UIKit
import CoreLocation
class MapLocationManager: NSObject, CLLocationManagerDelegate {
    var isFirstLoactionSucc : Bool = false
    
    var currentLocation : CLLocation!
    let locationManager : CLLocationManager = CLLocationManager()
    var latitude: CGFloat?
    var longitude: CGFloat?
    var admin: String!
    var locality: String!
    var thoroughfare: String!
    var countryCode: String!
    var countryName: String!
    var postalCode: String = ""
    
    var lastLocationDate: TimeInterval = 0.0
    
    static let shared = MapLocationManager()
    
    func startLocation() {
        isFirstLoactionSucc = false
        locationManager.delegate = self
        locationManager.distanceFilter = kCLLocationAccuracyKilometer
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestAlwaysAuthorization()
        if (CLLocationManager.locationServicesEnabled())
        {
            //允许使用定位服务的话，开启定位服务更新
            locationManager.startUpdatingLocation()
            print("定位开始")
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if locations.count > 0 {
            let locationInfo = locations.last!
            print("longitude:", locationInfo.coordinate.longitude, "latitude:", locationInfo.coordinate.latitude)
           
            lastLocationDate = Date().timeIntervalSince1970
            currentLocation = locations.last
            lonLatToCity()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("定位失败， \(error.localizedDescription)")
    }
    
    func lonLatToCity() {
        if isFirstLoactionSucc { return }
        let geocoder: CLGeocoder = CLGeocoder()
        latitude = CGFloat(currentLocation.coordinate.latitude)
        longitude = CGFloat(currentLocation.coordinate.longitude)
        
        print(currentLocation.coordinate.latitude)
        print(currentLocation.coordinate.longitude)
        
        geocoder.reverseGeocodeLocation(currentLocation) {[weak self] (placemark, error) -> Void in
            if(error == nil) {
                let array = placemark! as NSArray
                let mark = array.firstObject as! CLPlacemark
                if let addressDic = mark.addressDictionary {
                    print(addressDic["Country"] ?? "")
                    print(addressDic["CountryCode"] ?? "")
                    print(addressDic["State"] ?? "")
                    print(addressDic["City"] ?? "")
                    print(addressDic["Thoroughfare"] ?? "")
                    
                    self?.countryName = addressDic["Country"] as? String ?? ""
                    self?.countryCode = addressDic["CountryCode"] as? String ?? ""
                    self?.admin = addressDic["State"] as? String ?? ""
                    self?.locality = addressDic["City"] as? String ?? ""
                    self?.thoroughfare = addressDic["Thoroughfare"] as? String ?? ""
                    self?.postalCode = addressDic["postalCode"] as? String ?? ""
                    self?.locationManager.stopUpdatingLocation()
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "didUpdateLocations"), object: nil, userInfo: nil)
                }
                self?.isFirstLoactionSucc = true
            } else {
                print(error ?? "")
            }
        }
    }
}
