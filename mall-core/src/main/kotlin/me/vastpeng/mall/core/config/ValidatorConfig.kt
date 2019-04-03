package me.vastpeng.mall.core.config

import org.hibernate.validator.HibernateValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.validation.Validation
import javax.validation.Validator


@Configuration
class ValidatorConfiguration {
    @Bean
    fun validator(): Validator {
        val validatorFactory = Validation.byProvider(HibernateValidator::class.java)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory()

        return validatorFactory.validator
    }
}