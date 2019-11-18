package com.aliyun.solution.longvideo.bean;

public class RandomUserBean {

    /**
     * result : true
     * requestId : 0a2623bd-d493-4091-9acc-04a8f2967b5e
     * message : 生成长视频随机用户完成
     * code : 200
     * data : {"id":"32","userId":"360633","nickName":"Sue","avatarUrl":"http://live-appserver-sh.alivecdn.com/heads/04.png","gmtCreate":"2019-07-09 17:26:40.0","gmtModified":"","token":"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNjA2MzMiLCJpYXQiOjE1NjI2NjQ0MDAsInN1YiI6IntcInVzZUlkXCI6XCIzNjA2MzNcIn0ifQ.lcFheT1AQsLqugnEQSjIV5rkq4Ko7enBDyREVpznfMg"}
     */

    private String result;
    private String requestId;
    private String message;
    private String code;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 32
         * userId : 360633
         * nickName : Sue
         * avatarUrl : http://live-appserver-sh.alivecdn.com/heads/04.png
         * gmtCreate : 2019-07-09 17:26:40.0
         * gmtModified :
         * token : eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNjA2MzMiLCJpYXQiOjE1NjI2NjQ0MDAsInN1YiI6IntcInVzZUlkXCI6XCIzNjA2MzNcIn0ifQ.lcFheT1AQsLqugnEQSjIV5rkq4Ko7enBDyREVpznfMg
         */

        private String id;
        private String userId;
        private String nickName;
        private String avatarUrl;
        private String gmtCreate;
        private String gmtModified;
        private String token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(String gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public String getGmtModified() {
            return gmtModified;
        }

        public void setGmtModified(String gmtModified) {
            this.gmtModified = gmtModified;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
