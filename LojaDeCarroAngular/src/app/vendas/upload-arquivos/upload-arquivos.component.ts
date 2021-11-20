import { Component, OnInit } from '@angular/core';
import { UploadService } from 'src/app/services/upload.service';

@Component({
  selector: 'app-upload-arquivos',
  templateUrl: './upload-arquivos.component.html',
  styleUrls: ['./upload-arquivos.component.css']
})
export class UploadArquivosComponent implements OnInit {

  files : Set<File>;

  constructor(
    private uploadService : UploadService
  ) { }

  ngOnInit(): void {
  }

  onChange(e){
    const selectedFiles = <FileList>e.srcElement.files;

    const fileNames : string[] = [];
    this.files = new Set();

    for(let c = 0; c < selectedFiles.length; c ++) {
      fileNames.push(selectedFiles[c].name);
      this.files.add(selectedFiles[c]);
    }

    document.getElementById("customFileLabel")?.innerHTML == fileNames.join(", ");  
    console.log(fileNames)
  }

  onUpload(){
    console.log(this.files)
  }

}
