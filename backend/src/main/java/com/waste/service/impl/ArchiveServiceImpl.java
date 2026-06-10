package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.Archive;
import com.waste.mapper.ArchiveMapper;
import com.waste.service.ArchiveService;
import org.springframework.stereotype.Service;

@Service
public class ArchiveServiceImpl extends ServiceImpl<ArchiveMapper, Archive> implements ArchiveService {
}
