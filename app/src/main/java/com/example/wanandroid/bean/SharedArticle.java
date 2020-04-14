package com.example.wanandroid.bean;

import java.util.List;

public class SharedArticle {

    /**
     * data : {"coinInfo":{"coinCount":218,"level":3,"rank":1324,"userId":39275,"username":"H**hi"},"shareArticles":{"curPage":1,"datas":[{"apkLink":"","audit":1,"author":"","chapterId":494,"chapterName":"广场","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":true,"id":11389,"link":"https://www.jianshu.com/p/92bf67256243","niceDate":"刚刚","niceShareDate":"刚刚","origin":"","prefix":"","projectLink":"","publishTime":1578373144000,"selfVisible":0,"shareDate":1578373144000,"shareUser":"Hachi","superChapterId":494,"superChapterName":"广场Tab","tags":[],"title":"Activity的四种启动模式和应用场景?","type":0,"userId":39275,"visible":0,"zan":0}],"offset":0,"over":true,"pageCount":1,"size":20,"total":1}}
     * errorCode : 0
     * errorMsg :
     */

    private DataBean data;
    private int errorCode;
    private String errorMsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static class DataBean {
        /**
         * coinInfo : {"coinCount":218,"level":3,"rank":1324,"userId":39275,"username":"H**hi"}
         * shareArticles : {"curPage":1,"datas":[{"apkLink":"","audit":1,"author":"","chapterId":494,"chapterName":"广场","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":true,"id":11389,"link":"https://www.jianshu.com/p/92bf67256243","niceDate":"刚刚","niceShareDate":"刚刚","origin":"","prefix":"","projectLink":"","publishTime":1578373144000,"selfVisible":0,"shareDate":1578373144000,"shareUser":"Hachi","superChapterId":494,"superChapterName":"广场Tab","tags":[],"title":"Activity的四种启动模式和应用场景?","type":0,"userId":39275,"visible":0,"zan":0}],"offset":0,"over":true,"pageCount":1,"size":20,"total":1}
         */

        private CoinInfoBean coinInfo;
        private ShareArticlesBean shareArticles;

        public CoinInfoBean getCoinInfo() {
            return coinInfo;
        }

        public void setCoinInfo(CoinInfoBean coinInfo) {
            this.coinInfo = coinInfo;
        }

        public ShareArticlesBean getShareArticles() {
            return shareArticles;
        }

        public void setShareArticles(ShareArticlesBean shareArticles) {
            this.shareArticles = shareArticles;
        }

        public static class CoinInfoBean {
            /**
             * coinCount : 218
             * level : 3
             * rank : 1324
             * userId : 39275
             * username : H**hi
             */

            private int coinCount;
            private int level;
            private int rank;
            private int userId;
            private String username;

            public int getCoinCount() {
                return coinCount;
            }

            public void setCoinCount(int coinCount) {
                this.coinCount = coinCount;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public int getRank() {
                return rank;
            }

            public void setRank(int rank) {
                this.rank = rank;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }

        public static class ShareArticlesBean {
            /**
             * curPage : 1
             * datas : [{"apkLink":"","audit":1,"author":"","chapterId":494,"chapterName":"广场","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":true,"id":11389,"link":"https://www.jianshu.com/p/92bf67256243","niceDate":"刚刚","niceShareDate":"刚刚","origin":"","prefix":"","projectLink":"","publishTime":1578373144000,"selfVisible":0,"shareDate":1578373144000,"shareUser":"Hachi","superChapterId":494,"superChapterName":"广场Tab","tags":[],"title":"Activity的四种启动模式和应用场景?","type":0,"userId":39275,"visible":0,"zan":0}]
             * offset : 0
             * over : true
             * pageCount : 1
             * size : 20
             * total : 1
             */

            private int curPage;
            private int offset;
            private boolean over;
            private int pageCount;
            private int size;
            private int total;
            private List<Article> datas;

            public int getCurPage() {
                return curPage;
            }

            public void setCurPage(int curPage) {
                this.curPage = curPage;
            }

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }

            public boolean isOver() {
                return over;
            }

            public void setOver(boolean over) {
                this.over = over;
            }

            public int getPageCount() {
                return pageCount;
            }

            public void setPageCount(int pageCount) {
                this.pageCount = pageCount;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public List<Article> getDatas() {
                return datas;
            }

            public void setDatas(List<Article> datas) {
                this.datas = datas;
            }


        }
    }
}
