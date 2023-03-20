import React, { useEffect, useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import PostCommentCard from '../../../Cards/MyPosts/Comment/PostCommentCard';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';

const PostCommentModal = (props) => {
    const user = useUser();
    const {postId, show, emitHandleClose} = props;
    const emptyComment = {id: null, text: '', post: postId != null ? parseInt(postId):null, user: user.jwt};
    const [commentEntity, setCommentEntity] = useState(emptyComment);
    const [postComments, setPostComments] = useState([]);

    useEffect(() => {
        requestToPath(`/api/comments/${postId}/comment`, "GET", user.jwt)
            .then((cmtResponse) => { setPostComments(cmtResponse) })
            .catch((error) => { console.log(error); })
    }, [])

    function updateComment(value){
        const commentCopy = {...commentEntity};
        commentCopy.text = value;
        setCommentEntity(commentCopy);
    }

    function displayComments(){
        return postComments.sort((a,b) => b.id > a.id ? 1 : -1)
                    .map((eachCmt) => { 
                        return <div className='mb-3' key={eachCmt.id}> 
                                <PostCommentCard description={eachCmt.text}
                                createdOn={eachCmt.createdOn} 
                                firstname={eachCmt.user.firstname} 
                                lastname={eachCmt.user.lastname} /> 
                                </div>
                    })
    }

    function commentPostRequest(){        
        requestToPath(`/api/comments`, "POST", user.jwt, commentEntity)
            .then((commentResponse) => {
                const copyComments = [...postComments];
                copyComments.push(commentResponse);

                setPostComments(copyComments);
                setCommentEntity(emptyComment);
            })
            .catch((error) => { console.log(error); })
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
                        <Form.Control required  maxLength={255} placeholder = "add a comment" as="textarea" rows={2} onChange={(e) => updateComment(e.target.value)} value={commentEntity.text} />
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

export default PostCommentModal;