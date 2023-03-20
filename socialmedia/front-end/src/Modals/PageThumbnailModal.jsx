
import React from 'react';
import { useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';
import uploadFile from '../Service/uploadFile'

const PageThumbnailModal = (props) => {
    const user = useUser();
    const {show, emitHandleClose} = props;
    const {pageId} = useParams();
    const [file, setFile] = useState(null);
    const formData = new FormData();

    // `/api/pages/${pageId}/thumbnails`
    function sendUploadRequest(){
        formData.append("myFile", file);
        uploadFile(`/api/pageThumbnails/page/${pageId}/thumbnails`, "PUT", user.jwt, formData)
            .then((thumbResponse) => { })
            .catch((error) => {console.log(error);})
    }

    return (
        <div>
            <Modal size='lg' fullscreen show={show} onHide={emitHandleClose}>
                <Modal.Header className='custom-modalHeader'>
                    <Modal.Title ><h1>My Thumbnail</h1> </Modal.Title>
                </Modal.Header>
                <Modal.Body> 
                <Form.Group style={{border: "2px dashed grey", height:"50vh", display: "flex", alignItems: "flex-end", padding: "1rem 5rem"}}>
                    <Form.Control type='file' accept='image/*, video/*' onChange={(e) => {setFile(e.target.files[0]); }} />
                </Form.Group>
                    
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={() => {sendUploadRequest(); emitHandleClose();}} >Upload</Button>
                    <Button variant='secondary' onClick={emitHandleClose} >Close</Button>
                </Modal.Footer>
            </Modal> 
        </div>
    );
};

export default PageThumbnailModal;