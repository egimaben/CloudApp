package io.cloudboost.rss;

public class Tweet {
	private String name;
	private String id;
	private String imgUrl;
	private String tweet;

	public String getName() {
		return name;
	}

	public Tweet(String name, String id, String imgUrl, String tweet) {
		super();
		this.name = name;
		this.id = id;
		this.imgUrl = imgUrl;
		this.tweet = tweet;
	}
	
	@Override
	public String toString() {
		String str="name="+name+", id="+id+", imgUrl="+imgUrl+", tweet="+tweet;
		return str;
	}

	public Tweet() {
		// TODO Auto-generated constructor stub
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
}
