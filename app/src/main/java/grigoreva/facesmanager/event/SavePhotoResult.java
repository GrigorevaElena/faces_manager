package grigoreva.facesmanager.event;

/**
 * Created by Лена on 10.06.2017.
 */
public class SavePhotoResult implements MessageEvent<Boolean> {
    private boolean result;

    public SavePhotoResult(boolean result) {
        this.result = result;
    }
    @Override
    public Boolean getData() {
        return result;
    }
}
