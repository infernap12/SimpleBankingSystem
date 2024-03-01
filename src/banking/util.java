package banking;

public final class util {
    public static int luhnDigit(String AccountNumber) {
        // for now just returns 5 because best number

        // later stage, implement check digit method
        if (AccountNumber.length() == 16) {
            AccountNumber = AccountNumber.substring(0, AccountNumber.length() - 1);
        }
        int sum = 0;
        for (int i = 0; i < AccountNumber.length(); i++) {
            int digit = AccountNumber.charAt(i) - '0';
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        return (10 - (sum % 10)) % 10;
    }

    public static boolean isValidCard(String cardNumber) {
        int ld = luhnDigit(cardNumber);
        int lastDigit = Integer.parseInt(cardNumber.substring(cardNumber.length() - 1));
        return lastDigit == ld;
    }

}

