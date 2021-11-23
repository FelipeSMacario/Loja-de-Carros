import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { CarroService } from 'src/app/services/carro.service';
import { UploadService } from 'src/app/services/upload.service';

@Component({
  selector: 'app-upload-arquivos',
  templateUrl: './upload-arquivos.component.html',
  styleUrls: ['./upload-arquivos.component.css']
})
export class UploadArquivosComponent implements OnInit {

  files : Set<File>;
  formulario : FormGroup;
  id : number;
  carro : Carro;
  size : number;
  nomeArquivo : string;

  constructor(
    private uploadService : UploadService,
    private fb : FormBuilder,
    private carroService : CarroService,
    private activatedRoute : ActivatedRoute
  ) { }

  ngOnInit(): void {

    this.id = this.activatedRoute.snapshot.params["id"];
    this.findCarro();

    const valorAsync = new Promise((resolve, reject) => {
      setTimeout(() => resolve(this.formularioIMG()), 500)
    });
  }

  formularioIMG(){
    this.formulario = this.fb.group({
      id : [null],
      url : [null],
      carro : [this.carro]
    })
  }

  findCarro() : void {
    this.carroService.findCarroById(this.id).pipe(take(1)).subscribe({
      next : car => this.carro = car,
      error : err => console.log(err)
    })
  }

  onChange(e){
    const selectedFiles = <FileList>e.srcElement.files;

    const fileNames : string[] = [];
    this.files = new Set();

    for(let c = 0; c < selectedFiles.length; c ++) {
      fileNames.push(selectedFiles[c].name);
      this.files.add(selectedFiles[c]);
    }

    this.nomeArquivo = fileNames.join(", ");  
    this.size = this.files.size;
  }

  onUpload(){
    this.uploadService.upload(this.files, this.id).subscribe({
      next : enviado => console.log("enviado com sucesso", enviado),
      error : err => console.log(err)
    })
  }

  cliquei(){
    const data = new FormData();
    data.append("data",this.formulario.value);
    data.append("file", this.files[0]);

    console.log(data);
    console.log(this.formulario.value)
  }

}
