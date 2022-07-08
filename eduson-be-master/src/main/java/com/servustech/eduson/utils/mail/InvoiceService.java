package com.servustech.eduson.utils.mail;

import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutArea;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.users.data.IndividualService;
import com.servustech.eduson.features.account.users.data.LegalService;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.transactions.Transaction;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionPeriodsService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.MutablePair;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.AreaBreak;
import lombok.AllArgsConstructor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
@Service
@AllArgsConstructor
public class InvoiceService {

  public static final String REGULAR = "/fonts/OpenSans/OpenSans-Regular.ttf";
  public static final String REGULAR_ITALIC = "/fonts/OpenSans/OpenSans-Italic.ttf";
  public static final String BOLD = "/fonts/OpenSans/OpenSans-Bold.ttf";
  public static final String BOLD_ITALIC = "/fonts/OpenSans/OpenSans-BoldItalic.ttf";
  public static final String EDUSON_LOGO = "images/logo.svg";

  private final IndividualService individualService;
  private final LegalService legalService;
  private final PermissionsService permissionsService;
  private final SubscriptionPeriodsService subscriptionPeriodsService;

  private Cell getCell(Text text, TextAlignment alignment) {
    return getCell(new Paragraph(text), alignment);
  }

  private Cell getCell(IBlockElement ae, TextAlignment alignment) {

    Cell cell = new Cell().add(ae);
    cell.setPadding(0);
    cell.setTextAlignment(alignment);
    cell.setBorder(Border.NO_BORDER);
    cell.setPaddingRight(10);
    return cell;
  }

  private void addCell(Table table, String key, String value) throws IOException {
    PdfFont regular = PdfFontFactory.createFont(REGULAR, PdfEncodings.IDENTITY_H);
    table.addCell(getCell(
        new Paragraph(
            new Text(key + (key.length() > 0 && value.length() > 0 ? ": " : "")).setFont(regular).setFontSize(10))
                .setFixedLeading(10).setMarginBottom(5)
                .add(new Text(value).setFont(regular).setFontSize(10)),
        TextAlignment.LEFT));
  }

  private Cell addP(Cell cell, String key, String value) throws IOException {
    if (value == null) {
      value = "";
    }
    PdfFont regular = PdfFontFactory.createFont(REGULAR, PdfEncodings.IDENTITY_H);
    return addP(cell, key, new Text(value).setFont(regular));
  }

  private Cell addP(Cell cell, String key, Text value) throws IOException {
    PdfFont regular = PdfFontFactory.createFont(REGULAR, PdfEncodings.IDENTITY_H);
    return cell.add(
        new Paragraph(
            new Text(key + (key.length() > 0 && value.getText().length() > 0 ? ": " : "")).setFont(regular)
                .setFontSize(10))
                    .setFixedLeading(10).setMarginBottom(5)
                    .add(value.setFontSize(10)));
  }

  private String getPaymentMethod(PaymentType paymentType) {
    switch (paymentType) {
      case STRIPE:
        return "Card bancar";
      case TRANSFER:
        return "Transfer bancar";
      case NOT_NEEDED:
        return "Gratuit";
      default:
        return "Metodă de plată necunoscută";
    }
  }

  private String getPeriodName(Long periodId) {
    if (periodId != null) {
      return subscriptionPeriodsService.getPeriod(periodId).getName();
    } else {
      return "Perioadă nedeterminată";
    }
  }

  public static String getSKU(Permission permission) {
    String type = "";
    switch (permission.getProductType()) {
      case COURSE: type = "C"; break;
      case WEBINAR: type = "W"; break;
      case LIVE_EVENT: type = "E"; break;
      case MODULE: type = "M"; break;
      case SUBSCRIPTION: 
        type = "" + permission.getPeriodId() + "S"; break;
    }
    return type + permission.getProductId();
  }

  public Triple<File, String, String> create(String status, User user, List<Permission> permissions, Transaction transaction) {

    WriterProperties wp = new WriterProperties();
    wp.setPdfVersion(PdfVersion.PDF_2_0);
    String orderId = "Comanda_" + transaction.getId();
    String id = status.equals("paid") ? "EDU " + transaction.getInvoiceId() : orderId;
    String stripeId = status.equals("paid") ? transaction.getTransactionId() : ""+transaction.getId();
    File file = new File(id + "_" + stripeId + ".pdf");

    try (var os = new PdfWriter(new FileOutputStream(file));
        PdfWriter writer = new PdfWriter(os, wp);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument)) {

      PdfFont regular = PdfFontFactory.createFont(REGULAR, PdfEncodings.IDENTITY_H);
      PdfFont italic = PdfFontFactory.createFont(REGULAR_ITALIC, PdfEncodings.IDENTITY_H);
      PdfFont bold = PdfFontFactory.createFont(BOLD, PdfEncodings.IDENTITY_H);
      PdfFont boldItalic = PdfFontFactory.createFont(BOLD_ITALIC, PdfEncodings.IDENTITY_H);

      // margins are 36, except page numbering
      document.setBottomMargin(50 + 36);

      Table footerTable = new Table(1);
      footerTable.addCell(new Cell().setPadding(0).setBorder(Border.NO_BORDER)
          .add(new Paragraph(new Text("Eduson Education SRL").setFont(regular).setFontSize(8)).setFixedLeading(9)));
      footerTable.addCell(new Cell().setPadding(0).setBorder(Border.NO_BORDER)
          .add(new Paragraph(new Text("Capital Social: 200 lei").setFont(regular).setFontSize(8)).setFixedLeading(9)));
      footerTable.addCell(new Cell().setPadding(0).setBorder(Border.NO_BORDER)
          .add(new Paragraph(new Text("Adresa web: www.eduson.ro | tel: +4 0770 504 370 | email: suport@eduson.ro")
              .setFont(regular).setFontSize(8)).setFixedLeading(9)));
      footerTable.addCell(new Cell().setPadding(0).setPaddingTop(10).setBorder(Border.NO_BORDER).add(new Paragraph(
          new Text("Factura este valabilă și fără semnătură și ștampilă, conform art. 319 alin. 29 din Legea 227/2015")
              .setFont(italic).setFontSize(7))));
      
      pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(document, footerTable));

      float[] colWidths = { 1, 1 };
      Table upperTable = new Table(UnitValue.createPercentArray(colWidths)).setWidth(UnitValue.createPercentValue(100))
          .setMarginBottom(7); // 25
      // Eduson data TODO
      var edusonCell = new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
      addP(edusonCell, "", new Text("Eduson Education SRL").setFont(bold));
      addP(edusonCell, "C.I.F.", "43845270");
      addP(edusonCell, "Nr. ord. reg. com.", "J12/1107/2021");
      addP(edusonCell, "Sediul", "str. Brăduțului 20");
      addP(edusonCell, "", "400494 Cluj-Napoca");
      addP(edusonCell, "Județ", "Cluj");
      addP(edusonCell, "Cont IBAN", "RO60BTRLRONCRT0592396201");
      addP(edusonCell, "", "BANCA TRANSILVANIA S.A.");
      upperTable.addCell(edusonCell);
      // Eduson logo
      Image image = null;
      try {
        InputStream logoFile = new ClassPathResource("classpath:" + EDUSON_LOGO).getInputStream();
        image = SvgConverter.convertToImage(logoFile, pdfDocument);
      } catch (Exception e) {
        // development case:
        File logoFile2 = ResourceUtils.getFile("classpath:" + EDUSON_LOGO);
        image = SvgConverter.convertToImage(new FileInputStream(logoFile2), pdfDocument);
      }
      image.scaleToFit(200, 200);
      upperTable.addCell(getCell(new Paragraph().add(image), TextAlignment.RIGHT));
      document.add(upperTable);

      // titlu factură
      Table titleTable = new Table(1).setWidth(UnitValue.createPercentValue(100))
          .setMarginBottom(7); // 15
      Text title = (status.equals("paid") ? new Text("FACTURĂ FISCALĂ").setFont(bold)
          : new Text("FACTURĂ PROFORMĂ").setFont(bold))
              .setFontSize(30);
      titleTable.addCell(getCell(title, TextAlignment.LEFT));
      document.add(titleTable);

      // datele facturii si a clientului
      Table dateTable = new Table(2).setWidth(UnitValue.createPercentValue(100)).setMarginBottom(7); // 40

      String orderTimestamp = transaction.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("Europe/Bucharest")));
      String timestamp = transaction.getPaidAt() == null
        ? orderTimestamp
        : transaction.getPaidAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("Europe/Bucharest")));
      
      var invoiceCell = new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
      Table invoiceTable = new Table(2);
      addCell(invoiceTable, "", "Număr factură:");
      addCell(invoiceTable, "", id);
      addCell(invoiceTable, "", "Dată factură:");
      addCell(invoiceTable, "", timestamp);
      addCell(invoiceTable, "", "Număr comandă:");
      addCell(invoiceTable, "", orderId.toString());
      addCell(invoiceTable, "", "Dată comandă:");
      addCell(invoiceTable, "", orderTimestamp);
      addCell(invoiceTable, "", "Metodă de plată:");
      addCell(invoiceTable, "", getPaymentMethod(transaction.getPaymentType()));
      addCell(invoiceTable, "", "RRN:");
      addCell(invoiceTable, "", stripeId);
      invoiceCell.add(invoiceTable);
      dateTable.addCell(invoiceCell);

      var clientCell = new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
      var legal = legalService.findById(user.getId()).orElse(null);
      var individual = individualService.findById(user.getId()).orElse(null);
      var isPersonal = user.getInvoiceAddressPersonal();
      // TODO email and name is set even if neither of these is set, so use them!
      addP(clientCell, "", "Client:");
      if (isPersonal != null) {
        if (isPersonal) {
          addP(clientCell, "", user.getFullName());
          addP(clientCell, "CNP", individual.getCnp());
          addP(clientCell, "", individual.getAddress());
          addP(clientCell, "", individual.getZipCode() + " " + individual.getCity());
          addP(clientCell, "Județ", individual.getCounty());
          addP(clientCell, "", user.getEmail());
          addP(clientCell, "", individual.getPhone());
        } else {
          addP(clientCell, "", legal.getCompany());
          addP(clientCell, "", user.getFullName());
          addP(clientCell, "CUI", legal.getCui());
          addP(clientCell, "Reg.Com.", legal.getRegCom());
          addP(clientCell, "IBAN", legal.getIban());
          addP(clientCell, "", legal.getAddress());
          addP(clientCell, "", legal.getZipCode() + " " + legal.getCity());
          addP(clientCell, "Județ", legal.getCounty());
          addP(clientCell, "", user.getEmail());
          addP(clientCell, "", legal.getPhone());
        }
      }
      dateTable.addCell(clientCell);

      document.add(dateTable);

      // bunuri achiziționate
      Table table = new Table(5).setWidth(UnitValue.createPercentValue(100)).useAllAvailableWidth().setMarginBottom(7);
      var green = new DeviceRgb(20, 165, 174);
      var black = new DeviceRgb(0, 0, 0);
      var white = new DeviceRgb(255, 255, 255);
      var gray = new DeviceRgb(128, 128, 128);
      table.addHeaderCell(new Cell().setBackgroundColor(black)
          .add(new Paragraph(new Text("Produs").setFont(bold).setFontColor(white))));
      table.addHeaderCell(new Cell().setBackgroundColor(black)
          .add(new Paragraph(new Text("Cantitate").setFont(bold).setFontColor(white)).setTextAlignment(TextAlignment.RIGHT)));
      table.addHeaderCell(
          new Cell().setBackgroundColor(black).add(new Paragraph(new Text("Preț unitar net").setFont(bold).setFontColor(white)).setTextAlignment(TextAlignment.RIGHT)));
      table.addHeaderCell(
          new Cell().setBackgroundColor(black).add(new Paragraph(new Text("TVA").setFont(bold).setFontColor(white)).setTextAlignment(TextAlignment.RIGHT)));
      table.addHeaderCell(
          new Cell().setBackgroundColor(black).add(new Paragraph(new Text("Preț unitar cu TVA").setFont(bold).setFontColor(white)).setTextAlignment(TextAlignment.RIGHT)));

      Border priceBorderBottom = new SolidBorder(gray, 1);

      List<MutablePair<Permission, Long>> permissionsAmounts = new ArrayList<>();
      for (Iterator <Permission> it = permissions.iterator(); it.hasNext();) {
        Permission permission = it.next();
        boolean found = false;
        for (Iterator<MutablePair<Permission, Long>> it2 = permissionsAmounts.iterator(); it2.hasNext();) {
          var permissionAmount = it2.next();
          Permission p = permissionAmount.getLeft();
          if (
            p.getProductType().equals(permission.getProductType()) && 
            p.getProductId().equals(permission.getProductId()) && 
            (!p.getProductType().equals(ProductType.SUBSCRIPTION) || p.getPeriodId().equals(permission.getPeriodId()))
          ) {
            found = true;
            permissionAmount.setRight(permissionAmount.getRight() + 1);
            break;
          }
        }
        if (!found) {
          permissionsAmounts.add(MutablePair.of(permission, 1L));
        }
      }
      for (Iterator<MutablePair<Permission, Long>> it = permissionsAmounts.iterator(); it.hasNext();) {

        var permissionAmount = it.next();
        Permission permission = permissionAmount.getLeft(); 
        var period = getPeriodName(permission.getPeriodId());
        var product = permissionsService.getProduct2(permission.getProductType(),
            permission.getProductId()).getName();
        table.addCell(new Cell().add(new Paragraph(new Text(product).setFont(boldItalic)))
            .add(new Paragraph(new Text("Cod produs (SKU): " + getSKU(permission)).setFont(regular).setFontSize(10)))
            .setBorder(Border.NO_BORDER)
            .setBorderBottom(priceBorderBottom));
        table
            .addCell(new Cell().add(new Paragraph(new Text("" + permissionAmount.getRight()).setFont(boldItalic)).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                .setBorderBottom(priceBorderBottom));

        Float amount = permission.getValue();
        Double vat = amount * 19 / 119d;
        Double remains = amount - vat;
        table.addCell(
            new Cell().add(new Paragraph(new Text(String.format("%.2f", remains) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER).setBorderBottom(priceBorderBottom));
        table.addCell(
            new Cell().add(new Paragraph(new Text(String.format("%.2f", vat) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER).setBorderBottom(priceBorderBottom));
        table.addCell(
          new Cell().add(new Paragraph(new Text(String.format("%.2f", amount) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
              .setBorder(Border.NO_BORDER)
              .setBorderBottom(priceBorderBottom));
      }
      document.add(table);

      Table tableTotal = new Table(2)
        .setMarginBottom(7)
        .setHorizontalAlignment(HorizontalAlignment.RIGHT);

      Border totalBorderGray = new SolidBorder(gray, 2);
      Border totalBorder = new SolidBorder(black, 2);

      Float amount = transaction.getValue() / 100;
      Double vat = amount * 19 / 119d;
      Double remains = amount - vat;
      tableTotal.addCell(new Cell().add(new Paragraph(new Text("Valoare totală cu TVA").setFont(bold))).setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));
      tableTotal.addCell(new Cell().add(new Paragraph(new Text(String.format("%.2f", amount) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
          .setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));
      // if (voucher) {}
      tableTotal.addCell(new Cell().add(new Paragraph(new Text("Preț fără TVA").setFont(bold))).setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));
      tableTotal.addCell(new Cell().add(new Paragraph(new Text(String.format("%.2f", remains) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
          .setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));

      tableTotal.addCell(new Cell().add(new Paragraph(new Text("TVA").setFont(bold))).setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));
      tableTotal.addCell(new Cell().add(new Paragraph(new Text(String.format("%.2f", vat) + " lei").setFont(italic)).setTextAlignment(TextAlignment.RIGHT))
          .setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorderGray));

      tableTotal.addCell(new Cell().add(new Paragraph(new Text("Preț cu TVA").setFont(bold))).setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorder)
          .setBorderBottom(totalBorder));
      tableTotal.addCell(new Cell().add(new Paragraph(new Text(String.format("%.2f", amount) + " lei").setFont(bold)).setTextAlignment(TextAlignment.RIGHT))
          .setBorder(Border.NO_BORDER)
          .setBorderTop(totalBorder)
          .setBorderBottom(totalBorder));
      
      if (status.equals("ordered"))
      document.add(
          new Paragraph(new Text("Valabilitatea facturii proforme este de 3 zile.").setFont(regular))
              .setMarginBottom(7));
  
      Rectangle currentBox = document.getRenderer().getCurrentArea().getBBox();
      if (currentBox.getHeight() < 
        tableTotal.createRendererSubTree().setParent(
          document.getRenderer()).layout(new LayoutContext(new LayoutArea(1, new Rectangle(0, 0, 400, 10000.0F)))).getOccupiedArea().getBBox().getHeight()) {
        document.add(new AreaBreak());
      }
      document.add(tableTotal);

      stampPageFooter(document, pdfDocument, "");
      document.close();
    } catch (IOException e) { //
      System.out.println("Creating PDF failed " + e);
    }
    return Triple.of(file, id, stripeId);

  }

  public void stampPageFooter(Document doc, PdfDocument pdfDocument, String name)
  {
    int numberOfPages = pdfDocument.getNumberOfPages();
    for (int i = 1; i <= numberOfPages; i++) 
    {
       PdfPage page = pdfDocument.getPage(i);
       Rectangle pageSize = page.getPageSize();
       float pageX = pageSize.getRight() - doc.getRightMargin() - 40;
       float pageY = pageSize.getBottom() + 30 ;       
        // Write x of y to the right bottom
       Paragraph p = new Paragraph(String.format("%s / %s", i, numberOfPages));//.addStyle(PAGE_NUM_STYLE);
       doc.showTextAligned(p, pageX, pageY, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
       // write name to the left
       pageX = pageSize.getLeft() + doc.getLeftMargin();
       pageY = pageSize.getBottom() + 30;
       Paragraph para = new Paragraph(name)//.addStyle(PAGE_NUM_STYLE)
       .setMarginTop(10f);
       doc.showTextAligned(para, pageX, pageY ,  i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
   
    }
  }

  private static class TextFooterEventHandler implements IEventHandler {
    protected Document doc;
    protected Table table;

    public TextFooterEventHandler(Document doc, Table table) {
      this.doc = doc;
      this.table = table;
    }

    @Override
    public void handleEvent(Event currentEvent) {
      PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
      PdfDocument pdfDoc = docEvent.getDocument();
      PdfPage page = docEvent.getPage();
      int pageNumber = pdfDoc.getPageNumber(page);
      PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
      Table footerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
      // Table footerT2 = new Table(1).setWidth(UnitValue.createPercentValue(8))
      //   .addCell(
      //     new Cell().
      //     add(new Paragraph(new Text(""+pageNumber+"/")
      //     // .setFont(regular)
      //     .setFontSize(8)).setFixedLeading(9)));
      footerTable.addCell(new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(table));
//      footerTable.addCell(new Cell().setPadding(0).setBorder(Border.NO_BORDER).add(footerT2));
      new Canvas(canvas, new Rectangle(36, 36, page.getPageSize().getWidth() - 72, 50))
        .add(footerTable)
        .close();
    }
  }

}