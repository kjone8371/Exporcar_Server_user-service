//package com.kjone.kjoneuserservice;
//
//import org.apache.catalina.Context;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//
//    @Bean
//    public TomcatServletWebServerFactory tomcatFactory() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                // 기본 설정
//                context.setPath("/your-app");
//            }
//
//            public void customize(TomcatServletWebServerFactory factory) {
//                factory.addConnectorCustomizers(connector -> {
//                    connector.setProperty("maxThreads", "10000000");  // 최대 스레드 수 설정
//                    connector.setProperty("minSpareThreads", "100000");  // 최소 여유 스레드 수 설정
//                });
//            }
//        };
//    }
//}
