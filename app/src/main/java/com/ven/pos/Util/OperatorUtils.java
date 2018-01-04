package com.ven.pos.Util;

public class OperatorUtils {

    /**
     * 识别运营商
     */
    private static boolean isNum(String phoneNum) {
        for (int i = 0; i < phoneNum.length(); i++) {
            if (!Character.isDigit(phoneNum.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int execute(String phone) {
        String head1 = "";
        String head2 = "";
        phone = phone.trim();
        if (phone == null || phone.length() < 11) {
            System.out.println("length<11");
            return 0;
        } else {
            if (phone.startsWith("+")) {
                phone = phone.substring(1);
            }
            if (phone.startsWith("86")) {
                phone = phone.substring(2);
            }
        }
        if (phone.length() != 11) {
            System.out.println("not = 11");
            return 0;
        }
        if (!isNum(phone)) {
            System.out.println(" not num");
            return 0;
        }
        head1 = phone.substring(0, 3);
        head2 = phone.substring(0, 4);
        // 移动前三位
        boolean cmcctemp3 = head1.equals("134") || head1.equals("135")
                || head1.equals("136") || head1.equals("137")
                || head1.equals("138") || head1.equals("139")
                || head1.equals("147") || head1.equals("150")
                || head1.equals("151") || head1.equals("152")
                || head1.equals("157") || head1.equals("158")
                || head1.equals("159") || head1.equals("182")
                || head1.equals("183") || head1.equals("184")
                || head1.equals("178") || head1.equals("187")
                || head1.equals("188");
        if (cmcctemp3) {
            return 1;
        }
        boolean cmcctemp4 = head2.equals("1340") || head2.equals("1341")
                || head2.equals("1342") || head2.equals("1343")
                || head2.equals("1344") || head2.equals("1345")
                || head2.equals("1346") || head2.equals("1347")
                || head2.equals("1348") || head2.equals("1705");
        if (cmcctemp4) {
            return 1;
        }
        // 联通前3位
        boolean unicomtemp = head1.equals("130") || head1.equals("131")
                || head1.equals("132") || head1.equals("145")
                || head1.equals("155") || head1.equals("156")
                || head1.equals("176") || head1.equals("185")
                || head1.equals("186");
        if (unicomtemp) {
            return 2;
        }
        // unicom 4
        boolean unicomtemp4 = head1.equals("1709");
        if (unicomtemp4) {
            return 2;
        }
        // 电信前3位
        boolean telecomtemp = head1.equals("133") || head1.equals("153")
                || head1.equals("181") || head1.equals("177")
                || head1.equals("180") || head1.equals("189");

        if (telecomtemp) {
            return 3;
        }
        // telecom 4
        boolean telecomtemp4 = head1.equals("1700");
        if (telecomtemp4) {
            return 3;
        }

        return 0;
    }
}