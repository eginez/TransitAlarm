package com.eginez.transitalarm.config

import groovy.transform.builder.Builder

@Builder
class ViewConfig {
    URL url
    int width
    int height
    String name
}
