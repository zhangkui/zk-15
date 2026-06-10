package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.Certificate;
import com.waste.mapper.CertificateMapper;
import com.waste.service.CertificateService;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {
}
