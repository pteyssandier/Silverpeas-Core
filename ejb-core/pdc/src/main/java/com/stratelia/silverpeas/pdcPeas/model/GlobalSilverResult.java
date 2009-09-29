package com.stratelia.silverpeas.pdcPeas.model;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.stratelia.silverpeas.contentManager.GlobalSilverContent;
import com.stratelia.webactiv.searchEngine.model.MatchingIndexEntry;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.FileServerUtils;

/**
 * This class allows the result jsp page of the global search to show all
 * features (name, description, location)
 */
public class GlobalSilverResult extends GlobalSilverContent implements
    java.io.Serializable {
  private String titleLink = null;
  private String downloadLink = null;
  private String creatorName = null;
  private boolean exportable = false;
  private boolean selected = false;
  private MatchingIndexEntry indexEntry = null;

  public GlobalSilverResult(GlobalSilverContent gsc) {
    super(gsc.getName(), gsc.getDescription(), gsc.getId(), gsc.getSpaceId(),
        gsc.getInstanceId(), gsc.getDate(), gsc.getUserId());
    super.setLocation(gsc.getLocation());
    super.setURL(gsc.getURL());
    super.setScore(1);
  }

  public GlobalSilverResult(MatchingIndexEntry mie) {
    // super(mie.getTitle(), mie.getPreView(), mie.getObjectId(), null,
    // mie.getComponent(), mie.getCreationDate(), mie.getCreationUser());
    super(mie);
    indexEntry = mie;
    super.setType(mie.getObjectType());
    super.setScore(mie.getScore());

    if (mie.getThumbnail() != null) {
      super.setThumbnailURL(FileServerUtils.getUrl(null, mie.getComponent(),
          mie.getThumbnail(), mie.getThumbnailMimeType(), mie
              .getThumbnailDirectory()));

      String[] directory = new String[1];
      directory[0] = mie.getThumbnailDirectory();

      File image = new File(FileRepositoryManager.getAbsolutePath(mie
          .getComponent(), directory)
          + mie.getThumbnail());

      try {
        BufferedImage inputBuf = ImageIO.read(image);
        // calcul de la taille de la sortie
        double inputBufWidth;
        double inputBufHeight;
        double widthParam = 60;
        double width = widthParam;
        double ratio;
        double height;
        if (inputBuf.getWidth() > inputBuf.getHeight()) {
          inputBufWidth = inputBuf.getWidth();
          inputBufHeight = inputBuf.getHeight();
          width = widthParam;
          ratio = inputBufWidth / width;
          height = inputBufHeight / ratio;
        } else {
          inputBufWidth = inputBuf.getHeight();
          inputBufHeight = inputBuf.getWidth();
          height = widthParam;
          ratio = inputBufWidth / width;
          width = inputBufHeight / ratio;
        }
        super.setThumbnailWidth(Double.toString(width));
        super.setThumbnailHeight(Double.toString(height));
      } catch (Exception e) {
        super.setThumbnailHeight("60");
        super.setThumbnailWidth("60");
      }
    }
  }

  public MatchingIndexEntry getIndexEntry() {
    return indexEntry;
  }

  public String getTitleLink() {
    return titleLink;
  }

  public void setTitleLink(String link) {
    this.titleLink = link;
  }

  public String getDownloadLink() {
    return downloadLink;
  }

  public void setDownloadLink(String link) {
    this.downloadLink = link;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public boolean isExportable() {
    return exportable;
  }

  public void setExportable(boolean exportable) {
    this.exportable = exportable;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public boolean equals(Object other) {
    if (!(other instanceof GlobalSilverResult))
      return false;

    return (getId().equals(((GlobalSilverResult) other).getId()))
        && (getInstanceId()
            .equals(((GlobalSilverResult) other).getInstanceId()));
  }
}