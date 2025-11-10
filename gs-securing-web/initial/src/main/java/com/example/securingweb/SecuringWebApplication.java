package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;   // cần check đây
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SecuringWebApplication {

	// public static void main(String[] args) throws Throwable {
	// 	SpringApplication.run(SecuringWebApplication.class, args);
	// }

	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SecuringWebApplication.class, args);
		// ⭐ THÊM ĐOẠN NÀY - in ra tất cả beans
        System.out.println("========== ALL BEANS ==========");
        String[] beanNames = ctx.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.contains("Controller") || beanName.contains("controller")) {
                System.out.println("✅ Found: " + beanName);
            }
        }
        System.out.println("===============================");
    }
    /**
     * Định nghĩa PasswordEncoder Bean TẠI ĐÂY để đảm bảo nó được tạo 
     * trước khi AuthController cần nó, giải quyết lỗi "bean not found".
     */
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
