package omar.mohamed.socialphotoneighbour.utility;

import java.util.Date;

public class ImageInfo {
    private String id;
    private String title;
    private String image;
    private String description;
    private Date dateTaken;
    private Date dateUpload;
    private String ownerName;
    
    ImageInfo(String id, String title, String image, String description, Date dateTaken,
              Date dateUpload, String ownerName){
      this.id = id;
      this.title = title;
      this.image = image;
      this.description = description;
      this.dateTaken = dateTaken;
      this.dateUpload = dateUpload;
      this.ownerName = ownerName;
      
    }
    
    public String getId(){
      return this.id;
    }
    
    public String getTitle(){
      return this.title;
    }
    
    public String getImage(){
      return this.image;
    }
    
    public String getDescription(){
      return this.description;
    }
    
    public Date getDateTaken(){
      return this.dateTaken;
    }
    
    public Date getDateUpload(){
      return this.dateUpload;
    }
    
    public String getOwnerName(){
      return this.ownerName;
    }
    
    public void setId(String id){
      this.id = id;
    }
    
    public void setImage(String image){
      this.image = image;
    }
}
