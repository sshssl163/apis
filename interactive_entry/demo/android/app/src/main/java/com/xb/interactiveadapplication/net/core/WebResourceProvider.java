package com.xb.interactiveadapplication.net.core;

import java.io.InputStream;

/**
 */

public interface WebResourceProvider {

    InputStream provide(String url, String host, String path);


}
