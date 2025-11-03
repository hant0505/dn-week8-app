package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecuringWebApplication {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(SecuringWebApplication.class, args);
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
