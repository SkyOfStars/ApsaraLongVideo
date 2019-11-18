package com.aliyun.solution.longvideo.bean;

public class UpDateVersionBean {

    /**
     * result : true
     * requestId : 9c59ef11-d051-46e5-83f6-24b869cfcd7d
     * message : 根据条件获取工具包信息完成
     * code : 200
     * data : {"id":"10","toolKitName":"longVideo","url":"https://alivc-demo-cms.alicdn.com/versionProduct/installPackage/longVideo/longVideo_update_android.json","type":"1"}
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
         * id : 10
         * toolKitName : longVideo
         * url : https://alivc-demo-cms.alicdn.com/versionProduct/installPackage/longVideo/longVideo_update_android.json
         * type : 1
         */

        private String id;
        private String toolKitName;
        private String url;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getToolKitName() {
            return toolKitName;
        }

        public void setToolKitName(String toolKitName) {
            this.toolKitName = toolKitName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
