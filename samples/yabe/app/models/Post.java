package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import leodagdag.play2morphia.Model;

@Entity
public class Post extends Model {

	@Id
	public ObjectId id;
	
	public String title;
	
	public static Model.Finder<ObjectId,Post> find = new Model.Finder<ObjectId,Post>(ObjectId.class, Post.class);
}
