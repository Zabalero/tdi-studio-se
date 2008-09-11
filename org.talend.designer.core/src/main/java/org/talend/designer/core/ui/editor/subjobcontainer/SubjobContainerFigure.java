// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.subjobcontainer;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.talend.commons.utils.image.ColorUtils;
import org.talend.commons.utils.workbench.gef.SimpleHtmlFigure;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IProcess2;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.nodes.Node;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public class SubjobContainerFigure extends Figure {

    private SubjobContainer subjobContainer;

    private RoundedRectangle outlineFigure;

    private SimpleHtmlFigure titleFigure;

    private RoundedRectangle rectFig;

    private SubjobCollapseFigure collapseFigure;

    private Color mainColor;

    private boolean showTitle;

    private String title;

    private Color subjobTitleColor;

    /**
     * DOC nrousseau SubjobContainerFigure constructor comment.
     * 
     * @param model
     */
    public SubjobContainerFigure(SubjobContainer subjobContainerTmp) {
        setLayoutManager(new FreeformLayout());
        this.subjobContainer = subjobContainerTmp;

        outlineFigure = new RoundedRectangle();
        rectFig = new RoundedRectangle();
        titleFigure = new SimpleHtmlFigure();
        titleFigure.setOpaque(true);
        collapseFigure = new SubjobCollapseFigure();

        collapseFigure.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                IProcess2 process = subjobContainer.getProcess();
                if (!process.isReadOnly()) {
                    PropertyChangeCommand ppc = new PropertyChangeCommand(subjobContainer, EParameterName.COLLAPSED.getName(),
                            !subjobContainer.isCollapsed());
                    process.getCommandStack().execute(ppc);
                    reSelection();
                }
            }
        });

        initSubJobTitleColor();

        updateData();

        initializeSubjobContainer(subjobContainer.getSubjobContainerRectangle());
    }

    /**
     * yzhang Comment method "initSubJobTitleColor".
     */
    private void initSubJobTitleColor() {
        IElementParameter colorParameter = subjobContainer.getElementParameter(EParameterName.SUBJOB_TITLE_COLOR.getName());
        // Color titleColor = ColorUtils.SUBJOB_TITLE_COLOR;
        if (subjobContainer.getSubjobStartNode().getComponent().getName().equals("tPrejob")
                || subjobContainer.getSubjobStartNode().getComponent().getName().equals("tPostjob")) {
            // titleColor = ColorUtils.SPECIAL_SUBJOB_TITLE_COLOR;
        }
        Color defaultSubjobColor = subjobContainer.getDefaultSubjobColor(ColorUtils.SUBJOB_TITLE_COLOR_NAME,
                ColorUtils.SUBJOB_TITLE_COLOR);
        if (colorParameter.getValue() == null) {
            subjobTitleColor = defaultSubjobColor;
            String colorValue = ColorUtils.getColorValue(subjobTitleColor);
            colorParameter.setValue(colorValue);
        } else {
            String strRgb = (String) colorParameter.getValue();
            subjobTitleColor = ColorUtils.parseStringToColor(strRgb, defaultSubjobColor);
        }
    }

    private void reSelection() {
        // select the start node.
        if (subjobContainer.isCollapsed()) {
            IProcess2 process = subjobContainer.getProcess();
            if (process instanceof org.talend.designer.core.ui.editor.process.Process) {
                AbstractMultiPageTalendEditor editor = ((org.talend.designer.core.ui.editor.process.Process) process).getEditor();
                Node startNode = subjobContainer.getSubjobStartNode();
                if (startNode != null && editor != null) {
                    editor.selectNode(startNode);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.draw2d.Figure#paint(org.eclipse.draw2d.Graphics)
     */
    @Override
    public void paint(Graphics graphics) {
        graphics.setAlpha(100);
        super.paint(graphics);
    }

    public void initializeSubjobContainer(Rectangle rectangle) {
        Point location = this.getLocation();
        collapseFigure.setCollapsed(subjobContainer.isCollapsed());

        titleFigure.setText("<b> " + title + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
        Dimension preferedSize = titleFigure.getPreferredSize();
        preferedSize = preferedSize.getExpanded(0, 2);
        titleFigure.setSize(rectangle.width - preferedSize.height, preferedSize.height);
        titleFigure.setLocation(location);
        titleFigure.setVisible(showTitle);

        outlineFigure.setLocation(location);
        outlineFigure.setVisible(showTitle);
        outlineFigure.setBackgroundColor(subjobTitleColor);
        outlineFigure.setForegroundColor(subjobTitleColor);
        outlineFigure.setSize(rectangle.width, preferedSize.height);

        collapseFigure.setLocation(new Point(rectangle.width - preferedSize.height + location.x, location.y));
        collapseFigure.setSize(preferedSize.height, preferedSize.height);
        // collapseFigure.setBackgroundColor(new Color(null, 50, 50, 250));

        rectFig.setLocation(new Point(location.x, /* preferedSize.height + */location.y));
        rectFig.setSize(new Dimension(rectangle.width, rectangle.height /*- preferedSize.height*/));
        rectFig.setBackgroundColor(mainColor);
        rectFig.setForegroundColor(subjobTitleColor);
    }

    /**
     * yzhang Comment method "updateSubJobTitleColor".
     */
    public void updateSubJobTitleColor() {
        String rgb = (String) subjobContainer.getPropertyValue(EParameterName.SUBJOB_TITLE_COLOR.getName());
        if (rgb != null && !"".equals(rgb)) {
            subjobTitleColor = ColorUtils.parseStringToColor(rgb);
        } else {
            initSubJobTitleColor();
        }
        updateData();
    }

    /**
     * yzhang Comment method "updateData".
     */
    public void updateData() {

        showTitle = (Boolean) subjobContainer.getPropertyValue(EParameterName.SHOW_SUBJOB_TITLE.getName());

        title = (String) subjobContainer.getPropertyValue(EParameterName.SUBJOB_TITLE.getName());
        if (subjobContainer.getSubjobStartNode().getComponent().getName().equals("tPrejob")) {
            title = " Prejob:" + title;
            subjobContainer.getElementParameter(EParameterName.SHOW_SUBJOB_TITLE.getName()).setShow(false);
        } else if (subjobContainer.getSubjobStartNode().getComponent().getName().equals("tPostjob")) {
            title = " Postjob:" + title;
            subjobContainer.getElementParameter(EParameterName.SHOW_SUBJOB_TITLE.getName()).setShow(false);
        } else {
            subjobContainer.getElementParameter(EParameterName.SHOW_SUBJOB_TITLE.getName()).setShow(true);
        }
        String propertyValue = (String) subjobContainer.getPropertyValue(EParameterName.SUBJOB_TITLE_COLOR.getName());
        if (propertyValue == null || "".equals(propertyValue)) {
            subjobContainer.setPropertyValue(EParameterName.SUBJOB_TITLE_COLOR.getName(), subjobContainer.getDefaultSubjobColor(
                    ColorUtils.SUBJOB_TITLE_COLOR_NAME, ColorUtils.SUBJOB_TITLE_COLOR));
        }
        //
        propertyValue = (String) subjobContainer.getPropertyValue(EParameterName.SUBJOB_COLOR.getName());
        if (propertyValue == null || "".equals(propertyValue)) {
            subjobContainer.setPropertyValue(EParameterName.SUBJOB_COLOR.getName(), subjobContainer.getDefaultSubjobColor(
                    ColorUtils.SUBJOB_COLOR_NAME, ColorUtils.SUBJOB_COLOR));
        }

        mainColor = ColorUtils.parseStringToColor(propertyValue, ColorUtils.SUBJOB_COLOR);

        this.getChildren().remove(outlineFigure);
        this.getChildren().remove(rectFig);
        outlineFigure.getChildren().clear();
        rectFig.getChildren().clear();

        if (showTitle) {
            outlineFigure.add(titleFigure);
            outlineFigure.add(collapseFigure);
            add(rectFig, null, 0);
            add(outlineFigure, null, 1);
        } else {
            outlineFigure.add(titleFigure);
            rectFig.add(collapseFigure);
            add(outlineFigure, null, 0);
            add(rectFig, null, 1);
        }
    }

}
