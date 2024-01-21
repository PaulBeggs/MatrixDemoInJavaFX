package matrix.util;

public class TokenInfo {
    String token;
    int startIndex;

    TokenInfo(String token, int startIndex) {
        this.token = token;
        this.startIndex = startIndex;
    }

    // getters
    public String getToken() {
        return token;
    }

    public int getStartIndex() {
        return startIndex;
    }
}

