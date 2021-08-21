package com.tao.common.core.common.local;

import com.tao.common.core.common.other.ServiceUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author william
 */
public class I18nUtils {

    public static String getMessage(String code, String... args) {
        MessageSource messageSource = ServiceUtils.getService(MessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
