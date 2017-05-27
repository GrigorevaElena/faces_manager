package grigoreva.facesmanager.model;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by админ2 on 04.04.2017.
 */
public class PersonViewModel {
    //TODO возможно, будут еще фото
    @NonNull
    private String mPhotoPath;
    private String mSurname;
    private String mName;
    @Nullable
    private Integer mAge; /*may be unknown*/
    private Gender mGender;

    //TODO сколько координат будут определять точки лица?
    private int mFacePoints[][];

    @Nullable
    private Color mAvgFaceColor; /*avg color of current face, may be unknown*/

    @NonNull
    public String getPhotoPath() {
        return mPhotoPath;
    }

    public String getSurname() {
        return mSurname;
    }

    public void setSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Nullable
    public Integer getAge() {
        return mAge;
    }

    public void setAge(@Nullable Integer mAge) {
        this.mAge = mAge;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender mGender) {
        this.mGender = mGender;
    }

    public enum Gender {
        MALE(0), FEMALE(1), UNKNOWN(-1);

        private int value;

        Gender(int value) {
            this.value = value;
        }

        public static Gender fromValue(int value) {
            for (Gender gender : Gender.values()) {
                if (gender.getValue() == value) {
                    return gender;
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }
    }

    public PersonViewModel(@NonNull String filePath) {
        mPhotoPath = filePath;
    }

    public PersonViewModel(@NonNull String filePath, String surname, String name) {
        this(filePath);
        mSurname = surname;
        mName = name;
    }
}
