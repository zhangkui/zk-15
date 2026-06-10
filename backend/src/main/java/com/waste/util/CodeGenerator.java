package com.waste.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateWasteNo() {
        return "WS" + LocalDateTime.now().format(FORMATTER) + getRandomSuffix();
    }

    public static String generateOrderNo() {
        return "PO" + LocalDateTime.now().format(FORMATTER) + getRandomSuffix();
    }

    public static String generateFeeNo() {
        return "FE" + LocalDateTime.now().format(FORMATTER) + getRandomSuffix();
    }

    public static String generateVoucherNo() {
        return "VC" + LocalDateTime.now().format(FORMATTER) + getRandomSuffix();
    }

    private static String getRandomSuffix() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 4).toUpperCase();
    }
}
