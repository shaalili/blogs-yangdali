package cn.yangdali.pojo;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Article implements Serializable{

    private static final long serialVersionUID = 5207865247400761539L;

    private Integer articleId;

    private Integer articleUserId;

    private String articleTitle;

    private Integer articleViewCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;

    private Date articleCreateTime;

    private Date articleUpdateTime;

    private Integer articleIsComment;

    private Integer articleStatus;

    private Integer articleOrder;

    private String articleContent;

    private User user;

    private List<Tag> tagList;

    private List<Category> categoryList;

	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	public Integer getArticleUserId() {
		return articleUserId;
	}

	public void setArticleUserId(Integer articleUserId) {
		this.articleUserId = articleUserId;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public Integer getArticleViewCount() {
		return articleViewCount;
	}

	public void setArticleViewCount(Integer articleViewCount) {
		this.articleViewCount = articleViewCount;
	}

	public Integer getArticleCommentCount() {
		return articleCommentCount;
	}

	public void setArticleCommentCount(Integer articleCommentCount) {
		this.articleCommentCount = articleCommentCount;
	}

	public Integer getArticleLikeCount() {
		return articleLikeCount;
	}

	public void setArticleLikeCount(Integer articleLikeCount) {
		this.articleLikeCount = articleLikeCount;
	}

	public Date getArticleCreateTime() {
		return articleCreateTime;
	}

	public void setArticleCreateTime(Date articleCreateTime) {
		this.articleCreateTime = articleCreateTime;
	}

	public Date getArticleUpdateTime() {
		return articleUpdateTime;
	}

	public void setArticleUpdateTime(Date articleUpdateTime) {
		this.articleUpdateTime = articleUpdateTime;
	}

	public Integer getArticleIsComment() {
		return articleIsComment;
	}

	public void setArticleIsComment(Integer articleIsComment) {
		this.articleIsComment = articleIsComment;
	}

	public Integer getArticleStatus() {
		return articleStatus;
	}

	public void setArticleStatus(Integer articleStatus) {
		this.articleStatus = articleStatus;
	}

	public Integer getArticleOrder() {
		return articleOrder;
	}

	public void setArticleOrder(Integer articleOrder) {
		this.articleOrder = articleOrder;
	}

	public String getArticleContent() {
		return articleContent;
	}

	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

    
}