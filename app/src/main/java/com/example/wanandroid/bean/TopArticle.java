package com.example.wanandroid.bean;

import java.util.List;

public class TopArticle {


    /**
     * data : [{"apkLink":"","audit":1,"author":"拉勾","chapterId":249,"chapterName":"干货资源","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":true,"id":10721,"link":"http://mp.weixin.qq.com/s?__biz=MjM5MTE1NTQ4Mg==&amp;mid=502249487&amp;idx=1&amp;sn=8ede8ff099ee4e1fe6deeec5d2b1fe0a&amp;chksm=3ea2cc7a09d5456c7cbd50925f1d38ed3b7768ea8b5992f64bd6dd7d3087e731182dc33bdb3f#rd","niceDate":"刚刚","niceShareDate":"2019-12-06 12:24","origin":"","prefix":"","projectLink":"","publishTime":1577808000000,"selfVisible":0,"shareDate":1575606262000,"shareUser":"","superChapterId":249,"superChapterName":"干货资源","tags":[],"title":"阿里 P7 必备技术栈（1-5年必看）","type":1,"userId":-1,"visible":1,"zan":0},{"apkLink":"","audit":1,"author":"xiaoyang","chapterId":440,"chapterName":"官方","collect":false,"courseId":13,"desc":"<p>众所周知，Activity 旋转会造成 Activity 重建，非常多的 View 等状态都需要恢复。<\/p>\r\n<p>现在有一个问题：<\/p>\r\n<p>假设我这个 Activity 内部有一个异步线程正在下载东西，并不支持暂停恢复，断开就需要重新下载，但是 Activity 本身支持横竖屏切换显示。<\/p>\r\n<p>在 <strong>Activity 旋转重建<\/strong>的前提下，如果让这个异步线程继续下载，不会受到牵连呢？<\/p>","envelopePic":"","fresh":false,"id":10892,"link":"https://www.wanandroid.com/wenda/show/10892","niceDate":"2019-12-16 00:01","niceShareDate":"2019-12-16 00:01","origin":"","prefix":"","projectLink":"","publishTime":1576425718000,"selfVisible":0,"shareDate":1576425697000,"shareUser":"","superChapterId":440,"superChapterName":"问答","tags":[{"name":"问答","url":"/article/list/0?cid=440"}],"title":"每日一问 |  Activity 旋转啦，重新下载怪我咯？","type":1,"userId":2,"visible":1,"zan":0},{"apkLink":"","audit":1,"author":"鸿洋","chapterId":249,"chapterName":"干货资源","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":10285,"link":"http://gk.link/a/103Ei","niceDate":"2019-12-12 00:46","niceShareDate":"2019-11-15 01:01","origin":"","prefix":"","projectLink":"","publishTime":1576082805000,"selfVisible":0,"shareDate":1573750881000,"shareUser":"","superChapterId":249,"superChapterName":"干货资源","tags":[],"title":"本站福利，极客199 礼包自取","type":1,"userId":-1,"visible":1,"zan":0},{"apkLink":"","audit":1,"author":"xiaoyang","chapterId":360,"chapterName":"小编发布","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":9988,"link":"https://wanandroid.com/blog/show/2701","niceDate":"2019-12-01 22:02","niceShareDate":"2019-10-29 20:25","origin":"","prefix":"","projectLink":"","publishTime":1575208969000,"selfVisible":0,"shareDate":1572351926000,"shareUser":"","superChapterId":298,"superChapterName":"原创文章","tags":[],"title":"玩Android交流圈","type":1,"userId":-1,"visible":1,"zan":0}]
     * errorCode : 0
     * errorMsg :
     */

    private int errorCode;
    private String errorMsg;
    private List<Article> data;

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

    public List<Article> getData() {
        return data;
    }

    public void setData(List<Article> data) {
        this.data = data;
    }


}
