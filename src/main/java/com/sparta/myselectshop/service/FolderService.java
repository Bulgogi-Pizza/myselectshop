package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.security.UserDetailsImpl;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderService {

  private final FolderRepository folderRepository;

  public void addFolders(List<String> folderNames, UserDetailsImpl userDetails) {
    User user = userDetails.getUser();
    List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);

    List<Folder> folderList = new ArrayList<>();

    for (String folderName : folderNames) {
      if (!isExistFolderName(folderName, existFolderList)) {
        Folder folder = new Folder(folderName, user);
        folderList.add(folder);
      } else {
        throw new IllegalArgumentException("중복된 폴더명을 제거해주세요! 폴더명 : " + folderName);
      }
    }

    folderRepository.saveAll(folderList);
  }

  public List<FolderResponseDto> getFolders(User user) {
    List<Folder> folderList = folderRepository.findAllByUser(user);
    List<FolderResponseDto> responseDtoList = new ArrayList<>();

    for (Folder folder : folderList) {
      responseDtoList.add(new FolderResponseDto(folder));
    }

    return responseDtoList;
  }

  private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
    for (Folder existFolder : existFolderList) {
      if (existFolder.getName().equals(folderName)){
        return true;
      }
    }

    return false;
  }
}
