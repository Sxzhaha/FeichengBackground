package com.ruoyi.binZhou.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识图谱节点对象 ld_knowledge_nodes
 * 
 * @author ruoyi
 * @date 2025-08-01
 */
public class KnowledgeNodes extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 节点ID */
    @Excel(name = "节点ID")
    private String nodeId;

    /** 节点名称 */
    @Excel(name = "节点名称")
    private String nodeName;

    /** 节点类型 */
    @Excel(name = "节点类型")
    private String nodeType;

    /** 父节点ID */
    @Excel(name = "父节点ID")
    private String parentId;

    /** 节点描述 */
    @Excel(name = "节点描述")
    private String description;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setNodeId(String nodeId) 
    {
        this.nodeId = nodeId;
    }

    public String getNodeId() 
    {
        return nodeId;
    }

    public void setNodeName(String nodeName) 
    {
        this.nodeName = nodeName;
    }

    public String getNodeName() 
    {
        return nodeName;
    }

    public void setNodeType(String nodeType) 
    {
        this.nodeType = nodeType;
    }

    public String getNodeType() 
    {
        return nodeType;
    }

    public void setParentId(String parentId) 
    {
        this.parentId = parentId;
    }

    public String getParentId() 
    {
        return parentId;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("nodeId", getNodeId())
            .append("nodeName", getNodeName())
            .append("nodeType", getNodeType())
            .append("parentId", getParentId())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .toString();
    }
}
