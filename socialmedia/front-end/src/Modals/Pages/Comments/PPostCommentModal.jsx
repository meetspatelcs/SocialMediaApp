import React, { useEffect, useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import PPostCommentCards from '../../../Cards/Page/Comment/PPostCommentCards';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

const PPostCommentModal = (props) => {
    const user = useUser();
    const {compName, pageO, pagePostId, show, emitHandleClose} = props;
    const emptyComment = {id: null, text: '', pagePost: pagePostId != null ? parseInt(pagePostId):null, user: user.jwt};
    const [commentEntity, setCommentEntity] = useState(emptyComment);
    const [postComments, setPostComments] = useState([]);

    useEffect(() => {
        requestToPath(`/api/pageComments/${pagePostId}/comment`, "GET", user.jwt)
            .then((cmtResponse) => { setPostComments(cmtResponse); console.log(cmtResponse); })
            .catch((error) => { console.log(error); })
    }, [])

    function displayComments(){
        return postComments.sort((a,b) => b.id > a.id ? 1 : -1)
            .map((eachCmt) => {
                return <div key={eachCmt.id} className='mb-3'>
                    <PPostCommentCards compName={compName} pageO={pageO} description={eachCmt.text} 
                    createdOn={eachCmt.createdOn} 
                    commenterId={eachCmt.user.id}
                    firstname={eachCmt.user.firstname}  
                    lastname={eachCmt.user.lastname}    
                    />
                </div>
            })
    }

    function commentPostRequest(){
        requestToPath(`/api/pageComments`, "POST", user.jwt, commentEntity)
            .then((commentResponse) => {
                const copyComments = [...postComments];
                copyComments.push(commentResponse);

                setPostComments(copyComments);
                setCommentEntity(emptyComment);
            })
            .catch((error) => { console.log(error); })
    }

    function updateComment(value){
        const commentCopy = {...commentEntity};
        commentCopy.text = value;
        setCommentEntity(commentCopy);
    }

    return (
        <div>
            <Modal size='lg' show={show} onHide={emitHandleClose}>
                <Modal.Header closeButton>
                <Modal.Title>Comments</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div style={{height: "25rem", overflowY: "auto"}}>
                        {displayComments()}
                    </div>   
                    <Form.Group className="mb-3 mt-3">
                        {/* <Form.Label>Comment</Form.Label> */}
                        <Form.Control required  maxLength={255} placeholder = "add a comment" as="textarea" 
                        rows={2} onChange={(e) => updateComment(e.target.value)} value={commentEntity.text} />
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={ () => {commentPostRequest();} }>Post</Button>
                    <Button variant="secondary" onClick={emitHandleClose}>Close</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default PPostCommentModal;