//
//  LandPageController.swift
//  AdflyDemo
//
//  Created by 全尼古拉斯 on 2020/3/9.
//  Copyright © 2020 全尼古拉斯. All rights reserved.
//

import UIKit
import WebKit
class LandPageController: UIViewController {
    weak var webView: WKWebView!
    var url: URL!
    override func viewDidLoad() {
        super.viewDidLoad()
        webView = WKWebView()
        self.view.addSubview(webView)
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.topAnchor.constraint(equalTo: self.view.topAnchor).isActive = true
        webView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor).isActive = true
        webView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        webView.trailingAnchor.constraint(equalTo:self.view.trailingAnchor).isActive = true
        // Load URL
        webView.load(URLRequest(url: url))
        print("Start load URL: ", url ?? "")
    }
}
