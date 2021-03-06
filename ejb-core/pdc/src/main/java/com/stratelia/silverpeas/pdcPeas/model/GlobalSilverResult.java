/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stratelia.silverpeas.pdcPeas.model;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.silverpeas.util.ImageUtil;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.contentManager.GlobalSilverContent;
import com.stratelia.webactiv.searchEngine.model.MatchingIndexEntry;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.FileServerUtils;

/**
 * This class allows the result jsp page of the global search to show all features (name,
 * description, location)
 */
public class GlobalSilverResult extends GlobalSilverContent implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  private String titleLink = null;
  private String downloadLink = null;
  private String creatorName = null;
  private boolean exportable = false;
  private boolean selected = false;
  private MatchingIndexEntry indexEntry = null;
  private boolean hasRead = false; // marks a result as redden
  private int resultId = 0;
  private int hits = -1;

  public GlobalSilverResult(GlobalSilverContent gsc) {
    super(gsc.getName(), gsc.getDescription(), gsc.getId(), gsc.getSpaceId(),
        gsc.getInstanceId(), gsc.getDate(), gsc.getUserId());
    super.setLocation(gsc.getLocation());
    super.setURL(gsc.getURL());
    super.setScore(1);

    super.setThumbnailHeight(gsc.getThumbnailHeight());
    super.setThumbnailMimeType(gsc.getThumbnailMimeType());
    super.setThumbnailWidth(gsc.getThumbnailWidth());
    super.setThumbnailURL(gsc.getThumbnailURL());
  }

  public GlobalSilverResult(MatchingIndexEntry mie) {
    super(mie);
    indexEntry = mie;
    super.setType(mie.getObjectType());
    super.setScore(mie.getScore());

    if (mie.getThumbnail() != null) {
      if (mie.getThumbnail().startsWith("/")) {
        // case of a thumbnail picked up in a gallery
        super.setThumbnailURL(mie.getThumbnail());
        setThumbnailWidth("60");
      } else {
        // case of an uploaded image
        super.setThumbnailURL(FileServerUtils.getUrl(null, mie.getComponent(),
            mie.getThumbnail(), mie.getThumbnailMimeType(), mie.getThumbnailDirectory()));

        String[] directory = new String[1];
        directory[0] = mie.getThumbnailDirectory();

        File image = new File(FileRepositoryManager.getAbsolutePath(mie.getComponent(), directory)
            + mie.getThumbnail());

        String[] dimensions = ImageUtil.getWidthAndHeightByWidth(image, 60);
        if (!StringUtil.isDefined(dimensions[0])) {
          dimensions[0] = "60";
        }
        setThumbnailWidth(dimensions[0]);
        setThumbnailHeight(dimensions[1]);
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

  /**
   * indicates if a entry has been read
   * @return the hasRead
   */
  public boolean isHasRead() {
    return hasRead;
  }

  /**
   * @param hasRead the hasRead to set
   */
  public void setHasRead(boolean hasRead) {
    this.hasRead = hasRead;
  }

  public int getResultId() {
    return resultId;
  }

  public void setResultId(int resultId) {
    this.resultId = resultId;
  }

  public void setHits(int hits) {
    this.hits = hits;
  }

  public int getHits() {
    return hits;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof GlobalSilverResult)) {
      return false;
    }
    return (getId().equals(((GlobalSilverResult) other).getId()))
        && (getInstanceId().equals(((GlobalSilverResult) other).getInstanceId()));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
    hash = 29 * hash + (this.getInstanceId() != null ? this.getInstanceId().hashCode() : 0);
    return hash;
  }
}