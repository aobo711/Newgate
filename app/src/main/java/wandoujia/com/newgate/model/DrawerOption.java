package wandoujia.com.newgate.model;

/**
 * Created by jintian on 15/1/24.
 */
public class DrawerOption {
    private String icon;
    private String title;
    private String hint;
    private String value;

    public String getIcon(){
        return icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getHint(){
        return hint;
    }

    public void setHint(String hint){
        this.hint = hint;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
