//
//  AppDelegate.swift
//  AdflyDemo
//
//  Created by 全尼古拉斯 on 2020/3/9.
//  Copyright © 2020 全尼古拉斯. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        let sb = UIStoryboard.init(name: "ViewController", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier:"ViewController") as! ViewController
        let nav = UINavigationController(rootViewController: vc)
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = nav
        window?.makeKeyAndVisible()
        return true
    }

  
}

