
-injars       build/libs/App_no_obfuscation.jar

#cac thu vien
-injars       build/tmp_lib/cogover-rpc-5.0.1-SNAPSHOT.jar
-injars       build/tmp_lib/cogover-java-common-1.3.30-SNAPSHOT.jar

-outjars      _deploy/App.jar

#bat buoc phai them 2 dong nay, neu ko chay file sau khi lam roi se loi tiem an (ke ca build thanh cong)
-libraryjars build/tmp_lib/(**.jar;!module-info.class)
-libraryjars  <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars  <java.home>/jmods/java.logging.jmod(!**.jar;!module-info.class)
-libraryjars  <java.home>/jmods/java.sql.jmod(!**.jar;!module-info.class)
-libraryjars  <java.home>/jmods/java.naming.jmod(!**.jar;!module-info.class)
-libraryjars  <java.home>/jmods/java.xml.jmod(!**.jar;!module-info.class)

-printmapping proguard-app.map

#bo qua canh bao lien quan den lombok
-dontwarn lombok.**
-dontwarn org.apache.kafka.clients.**

#keep Main Class
-keep public class com.cogover.template.server.Start { *; }
-keep public class com.cogover.template.server.Stop { *; }

#keep entity, can keep ca *Annotation* (hibernate can)
-keepattributes *Annotation*
-keep class com.cogover.template.server.database.entity.** { *; }

#keep pojo
-keep class com.cogover.template.server.config.ServerConfig { *; }
-keep class com.cogover.template.server.config.ServerConfig$* { *; }
-keep class com.cogover.template.server.pojo.** { *; }

#keep cac class cho thu vien: cogover-rpc
#-keep class com.cogover.rpc.client.CallRequest { *; }
#-keep class com.cogover.rpc.packet.** { *; }
#-keep class com.cogover.rpc.server.RpcResult { *; }





















#keep de Springboot chay duoc ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ==>
-dontshrink
-keepdirectories
-keepclasseswithmembers public class * { public static void main(java.lang.String[]);}
-keepclassmembers enum * { *; }
-keep @org.springframework.stereotype.Service class *
-keep @org.springframework.stereotype.Repository class *
-keep @org.springframework.stereotype.Component class *
-keep @org.springframework.context.annotation.Configuration class *
-keep @org.springframework.context.annotation.Bean class *
-keepclassmembers class * {
     @org.springframework.beans.factory.annotation.Autowired *;
     @org.springframework.beans.factory.annotation.Qualifier *;
     @org.springframework.beans.factory.annotation.Value *;
     @org.springframework.beans.factory.annotation.Required *;
     @org.springframework.context.annotation.Bean *;
     @org.springframework.context.annotation.Primary *;
     @org.springframework.scheduling.annotation.Scheduled *;
     @org.springframework.boot.context.properties.ConfigurationProperties *;
     @org.springframework.boot.context.properties.EnableConfigurationProperties *;
     @org.springframework.security.access.prepost.PreAuthorize *;
     @javax.inject.Inject *;
     @javax.annotation.PostConstruct *;
     @javax.annotation.PreDestroy *;
}
-keep @org.springframework.cache.annotation.EnableCaching class *
-keep @org.springframework.context.annotation.Configuration class *
-keep @org.springframework.boot.context.properties.ConfigurationProperties class *
-keep @org.springframework.boot.autoconfigure.SpringBootApplication class *
-keepattributes *Annotation*
-keep interface * extends org.springframework.data.jpa.repository.JpaRepository{ *; } -keepattributes Signature
#keep de Springboot chay duoc ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ <==









