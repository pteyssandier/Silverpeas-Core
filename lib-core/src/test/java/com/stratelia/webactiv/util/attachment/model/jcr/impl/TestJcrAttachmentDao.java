package com.stratelia.webactiv.util.attachment.model.jcr.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import com.silverpeas.jcrutil.BasicDaoFactory;
import com.silverpeas.jcrutil.JcrConstants;
import com.silverpeas.util.MimeTypes;
import com.stratelia.webactiv.util.attachment.ejb.AttachmentPK;
import com.stratelia.webactiv.util.attachment.model.AttachmentDetail;
import com.stratelia.webactiv.util.attachment.model.jcr.JcrAttachmentDao;
import static com.silverpeas.util.PathTestUtil.*;

public class TestJcrAttachmentDao extends AbstractJcrTestCase {

  private static final String instanceId = "kmelia57";
  private static final String UPLOAD_DIR = BUILD_PATH + SEPARATOR + "uploads" +
      SEPARATOR + instanceId + SEPARATOR + "Attachment" + SEPARATOR + "tests" +
      SEPARATOR + "simpson" + SEPARATOR + "bart" + SEPARATOR;
  private Calendar calend;
  private JcrAttachmentDao jcrAttachmentDao;

  public void setJcrAttachmentDao(JcrAttachmentDao jcrAttachmentDao) {
    this.jcrAttachmentDao = jcrAttachmentDao;
  }

  protected void prepareUploadedFile(String fileName, String physicalName)
      throws IOException {
    InputStream in = null;
    FileOutputStream out = null;
    try {
      in = this.getClass().getClassLoader().getResourceAsStream(fileName);
      File destinationDir = new File(UPLOAD_DIR);
      destinationDir.mkdirs();
      File destinationFile = new File(destinationDir, physicalName);
      out = new FileOutputStream(destinationFile);
      int c = 0;
      byte[] buffer = new byte[8];
      while ((c = in.read(buffer)) >= 0) {
        out.write(buffer, 0, c);
      }
      out.close();
      out = null;
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  protected File getFile() {
    String fileUrl = TestJcrAttachmentDao.class.getClassLoader().getResource(
        "FrenchScrum.odp").toString().substring(6);
    File file = new File(fileUrl);
    assertNotNull(file);
    assertTrue(file.exists());
    return file;
  }

  protected void onSetUp() {
    calend = Calendar.getInstance();
    calend.set(Calendar.MILLISECOND, 0);
    calend.set(Calendar.SECOND, 0);
    calend.set(Calendar.MINUTE, 15);
    calend.set(Calendar.HOUR, 9);
    calend.set(Calendar.DAY_OF_MONTH, 12);
    calend.set(Calendar.MONTH, Calendar.MARCH);
    calend.set(Calendar.YEAR, 2008);
  }

  protected void onTearDown() throws Exception {
    clearRepository();
    File uploadDir = new File(UPLOAD_DIR);
    uploadDir.delete();

  }

  public void testCreateAttachmentNode() throws Exception {
    Session session = null;
    try {
      prepareUploadedFile("FrenchScrum.odp",
          "abf562dee7d07e1b5af50a2d1b3d724ef5a88869");
      session = BasicDaoFactory.getSystemSession();
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("frenchScrum.odp");
      attachment.setPhysicalName("abf562dee7d07e1b5af50a2d1b3d724ef5a88869");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      jcrAttachmentDao.createAttachmentNode(session, attachment, null);
      Node pathNode = session.getRootNode().getNode("attachments");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode("attachments");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode("attachments/" + instanceId);
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode(
          "attachments/" + instanceId + "/Attachment");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode(
          "attachments/" + instanceId + "/Attachment/tests");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode(
          "attachments/" + instanceId + "/Attachment/tests/simpson");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      pathNode = session.getRootNode().getNode(
          "attachments/" + instanceId + "/Attachment/tests/simpson/bart");
      assertNotNull(pathNode);
      assertEquals(JcrConstants.NT_FOLDER,
          pathNode.getPrimaryNodeType().getName());
      Node fileNode = session.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/frenchScrum.odp");
      assertNotNull(fileNode);
      assertEquals(JcrConstants.NT_FILE, fileNode.getPrimaryNodeType().getName());
      Node content = fileNode.getNode(JcrConstants.JCR_CONTENT);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertNotNull(content);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertNotNull(content.getProperty(JcrConstants.JCR_MIMETYPE));
      assertEquals(MimeTypes.MIME_TYPE_OO_PRESENTATION, content.getProperty(
          JcrConstants.JCR_MIMETYPE).getString());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  public void testUpdateAttachment() throws Exception {
    Session session = null;
    try {
      createTempFile(UPLOAD_DIR + "test.txt", "Ceci est un test.");
      session = BasicDaoFactory.getSystemSession();
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("test.txt");
      attachment.setPhysicalName("test.txt");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      jcrAttachmentDao.createAttachmentNode(session, attachment, null);
      // update of the content
      Node content = session.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test.txt/" +
          JcrConstants.JCR_CONTENT);
      assertNotNull(content);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertEquals(MimeTypes.MIME_TYPE_OO_PRESENTATION, content.getProperty(
          JcrConstants.JCR_MIMETYPE).getString());
      ByteArrayInputStream in = new ByteArrayInputStream("Ce test fonctionne.".
          getBytes());
      content.setProperty(JcrConstants.JCR_DATA, in);
      session.save();
      jcrAttachmentDao.updateAttachment(session, attachment, null);
      String result = readFile(UPLOAD_DIR + "test.txt");
      assertEquals("Ce test fonctionne.", result);
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  public void testIsNodeLocked() throws Exception {
    Session session = null;
    Session session2 = null;
    try {
      createTempFile(UPLOAD_DIR + "test.txt", "Ceci est un test.");
      session = BasicDaoFactory.getSystemSession();
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("test_update.txt");
      attachment.setPhysicalName("test.txt");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      jcrAttachmentDao.createAttachmentNode(session, attachment, null);
      Node fileNode = session.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test_update.txt");
      fileNode.addMixin(JcrConstants.MIX_LOCKABLE);
      session.save();
      fileNode = session.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test_update.txt");
      assertNotNull(fileNode);
      assertEquals(JcrConstants.NT_FILE, fileNode.getPrimaryNodeType().getName());
      assertFalse(fileNode.isLocked());
      fileNode.lock(false, true);
      assertTrue(fileNode.isLocked());
      session2 = BasicDaoFactory.getSystemSession();
      Node fileNode2 = session2.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test_update.txt");
      assertNotNull(fileNode2);
      assertEquals(JcrConstants.NT_FILE,
          fileNode2.getPrimaryNodeType().getName());
      assertTrue(fileNode2.isLocked());
      BasicDaoFactory.logout(session);
      session = null;
      assertFalse(fileNode2.isLocked());
    } finally {
      BasicDaoFactory.logout(session);
      BasicDaoFactory.logout(session2);
    }
  }

  public void testUpdateNodeAttachment() throws Exception {
    Session session = null;
    try {
      createTempFile(UPLOAD_DIR + "test.txt", "Ceci est un test.");
      session = BasicDaoFactory.getSystemSession();
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("test_update.txt");
      attachment.setPhysicalName("test.txt");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      jcrAttachmentDao.createAttachmentNode(session, attachment, null);
      // update of the content
      Node content = session.getRootNode().getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test_update.txt/" +
          JcrConstants.JCR_CONTENT);
      assertNotNull(content);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertEquals(MimeTypes.MIME_TYPE_OO_PRESENTATION, content.getProperty(
          JcrConstants.JCR_MIMETYPE).getString());
      assertEquals(
          "Ceci est un test.",
          readFileFromNode(
          session.getRootNode().getNode(
          "attachments/kmelia57/Attachment/tests/simpson/bart/100/test_update.txt")));
      createTempFile(UPLOAD_DIR + "test.txt", "Le test fonctionne.");
      jcrAttachmentDao.updateNodeAttachment(session, attachment, null);
      String result = readFile(UPLOAD_DIR + "test.txt");
      assertEquals("Le test fonctionne.", result);
      assertEquals("Le test fonctionne.", readFileFromNode(session.getRootNode().
          getNode(
          "attachments/" + instanceId +
          "/Attachment/tests/simpson/bart/100/test_update.txt")));
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  public void testDeleteAttachmentNode() throws Exception {
    Session session = null;
    try {
      createTempFile(UPLOAD_DIR + "testBis.txt", "Ceci est un test.");
      session = BasicDaoFactory.getSystemSession();
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("testBis.txt");
      attachment.setPhysicalName("testBis.txt");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      jcrAttachmentDao.createAttachmentNode(session, attachment, null);
      // delete the content
      Node content = session.getRootNode().getNode(
          "attachments/kmelia57/" +
          "Attachment/tests/simpson/bart/100/testBis.txt/" +
          JcrConstants.JCR_CONTENT);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertNotNull(content);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertEquals(MimeTypes.MIME_TYPE_OO_PRESENTATION, content.getProperty(
          JcrConstants.JCR_MIMETYPE).getString());
      jcrAttachmentDao.deleteAttachmentNode(session, attachment, null);
      Node folder = session.getRootNode().getNode(
          "attachments/" + instanceId + "/Attachment/tests/simpson/bart/100");
      assertNotNull(folder);
      try {
        session.getRootNode().getNode(
            "attachments/kmelia57/Attachment/tests/" +
            "simpson/bart/100/testBis.txt");
        fail("Node still in repository");
      } catch (PathNotFoundException pnfex) {
      }
      try {
        folder.getNode("testBis.txt");
        fail("Node still in repository");
      } catch (PathNotFoundException pnfex) {
      }
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  public void testAddFolder() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node rootNode = session.getRootNode();
      JcrAttachmentDaoImpl myDao = (JcrAttachmentDaoImpl) jcrAttachmentDao;
      Node folder = myDao.addFolder(rootNode, "essai");
      assertNotNull(folder);
      assertEquals(JcrConstants.NT_FOLDER, folder.getPrimaryNodeType().getName());
      Node folder2 = myDao.addFolder(rootNode, "essai");
      assertNotNull(folder2);
      assertEquals(JcrConstants.NT_FOLDER,
          folder2.getPrimaryNodeType().getName());
      assertEquals(folder, folder2);
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  public void testAddFile() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      Node rootNode = session.getRootNode();
      JcrAttachmentDaoImpl myDao = (JcrAttachmentDaoImpl) jcrAttachmentDao;
      createTempFile(UPLOAD_DIR + "testBis.txt", "Ceci est un test.");
      AttachmentPK pk = new AttachmentPK("100", instanceId);
      AttachmentDetail attachment = new AttachmentDetail();
      attachment.setAuthor("1");
      attachment.setInstanceId(instanceId);
      attachment.setPK(pk);
      attachment.setContext("tests,simpson,bart");
      attachment.setCreationDate(calend.getTime());
      attachment.setDescription("Attachment for tests");
      attachment.setLanguage("fr");
      attachment.setLogicalName("testBis.txt");
      attachment.setPhysicalName("testBis.txt");
      attachment.setOrderNum(2);
      attachment.setSize(975048);
      attachment.setType(MimeTypes.MIME_TYPE_OO_PRESENTATION);
      attachment.setTitle("Test OpenOffice");
      Node file = myDao.addFile(rootNode, attachment, null);
      assertNotNull(file);
      assertEquals(JcrConstants.NT_FILE, file.getPrimaryNodeType().getName());
      assertEquals("testBis.txt", file.getName());
      Node content = file.getNode(JcrConstants.JCR_CONTENT);
      assertNotNull(content);
      assertEquals(JcrConstants.NT_RESOURCE, content.getPrimaryNodeType().
          getName());
      assertEquals(MimeTypes.MIME_TYPE_OO_PRESENTATION, content.getProperty(
          JcrConstants.JCR_MIMETYPE).getString());
    } finally {
      BasicDaoFactory.logout(session);
    }
  }

  @Override
  protected void clearRepository() throws Exception {
    Session session = null;
    try {
      session = BasicDaoFactory.getSystemSession();
      session.getRootNode().getNode("attachments").remove();
      session.save();
    } catch (PathNotFoundException pex) {
    } finally {
      BasicDaoFactory.logout(session);
    }
  }
}
