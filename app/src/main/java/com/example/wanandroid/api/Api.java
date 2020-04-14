package com.example.wanandroid.api;



import com.example.wanandroid.bean.Article;
import com.example.wanandroid.bean.ArticleList;
import com.example.wanandroid.bean.Banner;
import com.example.wanandroid.bean.Collect;
import com.example.wanandroid.bean.CollectWeb;
import com.example.wanandroid.bean.CommonWeb;
import com.example.wanandroid.bean.HotKey;
import com.example.wanandroid.bean.ProjectTree;
import com.example.wanandroid.bean.ResponseBase;
import com.example.wanandroid.bean.SharedArticle;
import com.example.wanandroid.bean.TopArticle;
import com.example.wanandroid.bean.TreeList;
import com.example.wanandroid.bean.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    /**
     * 获取首页文章列表
     *
     */
    @GET("article/list/{page}/json")
    Observable<ArticleList> getHomeArticleList(@Path("page") int page);

    /**
     * 获取首页轮播图
     *
     */
    @GET("banner/json")
    Observable<Banner> getHomeBanner();

    /**
     * 获取常用网站
     *
     */
    @GET("friend/json")
    Observable<CommonWeb> getCommonWeb();

    /**
     * 搜索热词
     *
     */
    @GET("hotkey/json")
    Observable<HotKey> getHotKey();

    /**
     * 获取置顶文章
     */
    @GET("article/top/json")
    Observable<TopArticle> getTopArticleList();

    /**
     * 获取体系分类二级列表
     */
    @GET("tree/json")
    Observable<TreeList> getTreeList();

    /**
     * 获取二级列表下的文章
     */
    @GET("article/list/{page}/json")
    Observable<ArticleList> getArticleListByTree(@Path("page") int page, @Query("cid") int cid);

    /**
     * 查询作者的文章列表
     */
    @GET("article/list/{page}/json")
    Observable<ArticleList> getArticleByAuthor(@Path("page") int page, @Query("author") String author);

    /**
     * 获取项目分类列表
     */
    @GET("project/tree/json")
    Observable<ProjectTree> getProjectList();

    /**
     * 项目列表数据
     *
     */
    @GET("project/list/{page}/json")
    Observable<ArticleList> getProjectById(@Path("page") int page, @Query("cid") int cid);

    /**
     * 登录
     *
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<User> userLogin(@Field("username") String username, @Field("password") String password);

    /**
     * 注册
     *
     */
    @FormUrlEncoded
    @POST("user/register")
    Observable<User> userRegist(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);

    /**
     * 退出
     *
     */
    @GET("user/logout/json")
    Observable<User> userLogout();

    /**
     * 查询收藏信息
     *
     */
    @GET("lg/collect/list/{page}/json")
    Observable<Collect> selectCollect(@Path("page") int page);

    /**
     *收藏站内文章
     */
    @POST("lg/collect/{id}/json")
    Observable<ResponseBase> collectArticle(@Path("id") int id);

    /**
     * 收藏站外文章
     *
     */
    @FormUrlEncoded
    @POST("lg/collect/add/json")
    Observable<Collect.DataBean.DatasBean> collectArticleByOther(@Field("title") String title, @Field("author") String author,
                                                           @Field("link") String link);

    /**
     * 在首页文章列表中取消收藏
     *
     */
    @POST("lg/uncollect_originId/{id}/json")
    Observable<ResponseBase> cancelCollectionInArticle(@Path("id") int id);

    /**
     * 在个人收藏中取消收藏
     *
     */
    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json")
    Observable<ResponseBase> cancelCollectionInMyCollect(@Path("id") int id, @Field("originId") int originId);

    /**
     * 收藏网站列表
     *
     */
    @GET("lg/collect/usertools/json")
    Observable<CollectWeb> getCollectWebList();

    /**
     * 添加收藏网址
     *
     */
    @FormUrlEncoded
    @POST("lg/collect/addtool/json")
    Observable<CollectWeb> addCollectWeb(@Field("name") String name, @Field("link") String url);

    /**
     * 编辑收藏网址
     */
    @FormUrlEncoded
    @POST("lg/collect/updatetool/json")
    Observable<CollectWeb> updateCollectWeb(@Field("id") int id, @Field("name") String name,
                                      @Field("link") String url);

    /**
     * 删除网址
     *
     */
    @FormUrlEncoded
    @POST("lg/collect/deletetool/json")
    Observable<CollectWeb> deleteCollectWeb(@Field("id") int id);

    /**
     * 关键词搜索
     *
     */
    @FormUrlEncoded
    @POST("article/query/{page}/json")
    Observable<ArticleList> searchByKey(@Path("page") int page, @Field("k") String key);

    /**
     * 获取广场列表
     *
     */
    @GET("user_article/list/{page}/json")
    Observable<ArticleList> getSquareList(@Path("page")int page);

    /**
     * 获取自己的分享列表
     *
     */
    @GET("user/lg/private_articles/{page}/json")
    Observable<SharedArticle> getSharedArticle(@Path("page") int page);

    /**
     *
     * 导航数据
     */
    @GET("navi/json")
    Observable<ArticleList> getNavigationList();

    /**
     * 添加分享
     */
    @FormUrlEncoded
    @POST("lg/user_article/add/json")
    Observable<ResponseBase> addShareArticle(@Field("title")final String title,@Field("link")final String url);

    /**
     *删除自己的分享
     */
    @POST("lg/user_article/delete/{path}/json")
    Observable<ResponseBase> deleteShareArticle(@Path("path")String articleId);
}
