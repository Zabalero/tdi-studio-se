// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.xmlmap.parts.directedit;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.talend.designer.xmlmap.parts.AbstractInOutTreeEditPart;
import org.talend.designer.xmlmap.parts.TreeNodeEditPart;

/**
 * DOC talend class global comment. Detailled comment
 */
public class XmlMapNodeCellEditorLocator implements CellEditorLocator {

    private Figure figure;

    private TreeNodeEditPart treeNodePart;

    private Rectangle treeBounds;

    public XmlMapNodeCellEditorLocator(Figure figure, TreeNodeEditPart treeNodePart) {
        this(figure);
        this.treeNodePart = treeNodePart;
    }

    public XmlMapNodeCellEditorLocator(Figure figure) {
        this.figure = figure;
    }

    public void relocate(CellEditor celleditor) {
        Control control = celleditor.getControl();
        Rectangle copy = figure.getBounds().getCopy();

        if (treeNodePart != null) {
            AbstractInOutTreeEditPart inOutTreeEditPart = treeNodePart.getInOutTreeEditPart(treeNodePart);
            if (inOutTreeEditPart != null) {
                Rectangle treeBounds = inOutTreeEditPart.getFigure().getBounds();
                if (copy.x < treeBounds.x) {
                    copy.width = copy.x + copy.width - treeBounds.x;
                    copy.x = treeBounds.x + 1;
                }

                if (copy.x + copy.width > treeBounds.x + treeBounds.width) {
                    copy.width = copy.width - ((copy.x + copy.width) - (treeBounds.x + treeBounds.width)) - 1;
                }

            }
        }

        figure.translateToAbsolute(copy);
        if (control instanceof Text) {
            Text text = (Text) control;
            text.setBounds(copy.x + 1, copy.y + 1, copy.width - 1, copy.height - 1);
        } else {
            Composite text = (Composite) control;
            text.setBounds(copy.x + 1, copy.y + 1, copy.width - 1, copy.height - 1);
        }

    }

    public Figure getFigure() {
        return this.figure;
    }

}
