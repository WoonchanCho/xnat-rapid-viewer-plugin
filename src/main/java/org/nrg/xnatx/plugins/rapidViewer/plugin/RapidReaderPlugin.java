/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.plugin;

import org.nrg.framework.annotations.XnatPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@XnatPlugin(value = "rapidViewerPlugin", name = "Rapid Reader Plugin", entityPackages = "org.nrg.xnatx.plugins.rapidViewer.entities", logConfigurationFile = "org/nrg/xnatx/plugins/rapid-reader-logback.xml")
@ComponentScan({"org.nrg.xnatx.plugins.rapidViewer.repositories",
                "org.nrg.xnatx.plugins.rapidViewer.rest",
                "org.nrg.xnatx.plugins.rapidViewer.services.impl"})
@Slf4j
public class RapidReaderPlugin {
    public RapidReaderPlugin() {
        log.info("Creating the RapidReaderPlugin configuration class");
    }

    @Bean
    public String templatePluginMessage() {
        return "This comes from deep within the template plugin.";
    }
}
