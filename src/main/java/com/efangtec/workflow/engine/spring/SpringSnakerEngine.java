/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.efangtec.workflow.engine.spring;

import com.efangtec.workflow.engine.core.SnakerEngineImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Properties;

/**
 * spring环境使用的SnakerEngine实现类，主要接收spring的applicationContext对象
 * @author yuqs
 * @since 1.0
 */
public class SpringSnakerEngine extends SnakerEngineImpl
        implements InitializingBean, ApplicationContextAware {
	private static  ApplicationContext applicationContext;
    private Properties properties;

	public void afterPropertiesSet() throws Exception {
        com.efangtec.workflow.engine.spring.SpringConfiguration configuration = new com.efangtec.workflow.engine.spring.SpringConfiguration(applicationContext);
        if(properties != null) configuration.initProperties(properties);
        configuration.parser();
	}
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
        SpringSnakerEngine.applicationContext = applicationContext;
	}

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
