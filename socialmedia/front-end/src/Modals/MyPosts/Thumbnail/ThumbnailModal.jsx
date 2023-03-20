import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, ProgressBar, Toast } from 'react-bootstrap';
import axios from 'axios';
import { useUser } from '../../../UserProvider/UserProvider';


const ThumbnailModal = (props) => {
    const user = useUser();
    const {show, emitThumbnailClose} = props;
    const formData = new FormData();

    const [file, setFile] = useState(null);
    const [fileUploaded, setFileUploaded] = useState(0);
    const [isFileValid, setIsFileValid] = useState(false);

    const max_img_size = 8388608; // 8mb
    
    axios.defaults.headers.common = {'Authorization': `Bearer ${user.jwt}`};

    useEffect(() => {
        console.log(file);
        if(file !== null && getFileTypeValidation(file.type.split("/")[0])){         
            setIsFileValid(true);
        }
        else{
            setFile(null);
        }
    },[file]);

   
    function getFileTypeValidation(isType){
        if(isType === 'image'){
            if(file.size > max_img_size){
                alert("File size is too big, max size allowed is 8MB!"); 
                return false;
            }
           return true;  
        }
        alert(isType + " file type is invalid!");
        return false; 
    }

    function requestToUpdateThumbnail(){
        if(file === null){
            return alert("select a file");
        }  
        if((file === null) || ((file !== null) && isFileValid)){
            if(file !== null)
                formData.append("myFile", file);    
            
            axios({url: `/api/userThumbnails`, method: "PUT", data: formData, 
            onUploadProgress: (fileRes) => {
                const {loaded, total} = fileRes;
                let myPercent = Math.round((loaded*100)/total);

                if(myPercent < 100) { setFileUploaded(myPercent); }
            }
            })
            .then((fileResponse) => {
                setFileUploaded(100);
                setTimeout(() => {setFileUploaded(0); setFile(null); emitThumbnailClose();}, 1000);
                window.location.reload();
            })
            .catch((error) => {console.log(error);})
        }     
    }

    return (
        <div>
            <Modal size='lg' show={show} onHide={emitThumbnailClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title> <h1>Thumbnail</h1> </Modal.Title>
                </Modal.Header>

                <Modal.Body> 
                    <div className="mt-3">
                        <Form.Control type='file' accept='image/*' onChange={(e) => {setFile(e.target.files[0]); }} />
                    </div>
  
                    { fileUploaded > 0 && <ProgressBar striped now={fileUploaded} active="true" label={`${fileUploaded}%`} /> }
                </Modal.Body>

                <Modal.Footer>
                    {fileUploaded === 0 &&
                        <>
                            <Button onClick={ () => {requestToUpdateThumbnail()} }> Upload </Button>
                            <Button variant="secondary" onClick={emitThumbnailClose}>
                                Cancel
                            </Button>
                        </>}
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default ThumbnailModal;