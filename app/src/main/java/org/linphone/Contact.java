package org.linphone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.linphone.compatibility.Compatibility;

import com.roamtech.telephony.roamapp.bean.ContactPhone;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * @author xiaobo.cui 2014年11月24日 下午5:36:29
 */
public class Contact implements Serializable {
    private static final long serialVersionUID = 3790717505065723499L;

    public String name;
    public String number;
    public String simpleNumber;
    public String sortKey;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((sortKey == null) ? 0 : sortKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Contact other = (Contact) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (sortKey == null) {
            if (other.sortKey != null)
                return false;
        } else if (!sortKey.equals(other.sortKey))
            return false;
        return true;
    }

    private String id;
    //	private String name;
    private transient Uri photoUri;
    private transient Uri thumbnailUri;
    private transient Bitmap photo;
    private List<ContactPhone> numbersOrAddresses;
    private boolean hasFriends;

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    private long photoId = -1;

    public Contact(String id, String name, String number, String sortKey) {
        super();
        this.id = id;
        this.name = name;
        this.number = number;
        this.sortKey = sortKey;
        if (number != null) {
            this.simpleNumber = number.replaceAll("\\-|\\s", "");
        }
        this.photoUri = null;
        this.thumbnailUri = null;
        this.hasFriends = false;
    }

    public Contact(String id, String name, String number, String sortKey, Uri photo, Uri thumbnail) {
        super();
        this.id = id;
        this.name = name;
        this.number = number;
        this.sortKey = sortKey;
        if (number != null) {
            this.simpleNumber = number.replaceAll("\\-|\\s", "");
        }
        this.photoUri = photo;
        this.thumbnailUri = thumbnail;
        this.photo = null;
        this.hasFriends = false;
    }

    public Contact(String id, String name, String number, String sortKey, Uri photo, Uri thumbnail, Bitmap picture) {
        super();
        this.id = id;
        this.name = name;
        this.number = number;
        this.sortKey = sortKey;
        if (number != null) {
            this.simpleNumber = number.replaceAll("\\-|\\s", "");
        }
        this.photoUri = photo;
        this.thumbnailUri = thumbnail;
        this.photo = picture;
        this.hasFriends = false;
    }


    public Contact() {
        // TODO Auto-generated constructor stub
    }

    public boolean hasFriends() {
        return hasFriends;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public List<ContactPhone> getNumbersOrAddresses() {
        if (numbersOrAddresses == null)
            numbersOrAddresses = new ArrayList<ContactPhone>();
        return numbersOrAddresses;
    }

    public void refresh(ContentResolver cr) {
        this.numbersOrAddresses = Compatibility.extractContactNumbersAndAddresses(id, cr);
        /*LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
        if(lc != null && lc.getFriendList() != null) {
			for (LinphoneFriend friend :lc.getFriendList()){
				if (friend.getRefKey().equals(id)) {
					hasFriends = true;
					this.numbersOrAddresses.add(friend.getAddress().asStringUriOnly());
				}
			}
		}*/
        this.name = Compatibility.refreshContactName(cr, id);
    }
}
