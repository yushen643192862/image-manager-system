package com.imageplatform.service.impl;

import com.imageplatform.dto.Request.TagCreateRequest;
import com.imageplatform.dto.Request.TagDeleteRequest;
import com.imageplatform.dto.Request.TagGetRequest;
import com.imageplatform.dto.Response.TagGetResponse;
import com.imageplatform.entity.Tag;
import com.imageplatform.mapper.ImageTagMapper;
import com.imageplatform.mapper.TagMapper;
import com.imageplatform.service.TagService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;
    private final ImageTagMapper imageTagMapper;

    public TagServiceImpl(TagMapper tagMapper,  ImageTagMapper imageTagMapper) {
        this.tagMapper = tagMapper;
        this.imageTagMapper = imageTagMapper;
    }

    @Override
    public TagGetResponse getTag(Integer userId) {
        List<Tag> tags = tagMapper.getTagsByUserId(userId);
        for (Tag tag : tags) {
            tag.setNumber(imageTagMapper.getCountByTagId(tag.getId()));
        }
        TagGetResponse tagGetResponse = new TagGetResponse();
        tagGetResponse.setTags(tags);
        return tagGetResponse;
    }
    @Override
    public Void deleteTag(TagDeleteRequest request){
        Integer tagid = request.getTagId();
        imageTagMapper.deleteImageTagByTagId(tagid);
        tagMapper.deleteTagByTagId(tagid);
        return null;
    }
    @Override
    public LocalDateTime createTag(TagCreateRequest request){
        String name = request.getName();
        LocalDateTime created = LocalDateTime.now();
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreatedBy(request.getUserId());
        tag.setCreatedTime(created);
        tagMapper.insertTag(tag);
        return created;
    }

}
