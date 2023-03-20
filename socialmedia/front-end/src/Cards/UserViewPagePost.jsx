import React, { useEffect, useRef, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';
import PageImage from './PageMedia/PageImage';
import PageVideo from './PageMedia/PageVideo';
import PPostCommentModal from '../Modals/Pages/Comments/PPostCommentModal';

const UserViewPagePost = (props) => {
    const user = useUser();
    const {pageId, userId} = useParams();
    const navigate = useNavigate();
    const {post} = props;
    const myPostId = post.id;

    const [timeCollapsed, setTimeCollapsed] = useState(null);
    const [currPost, setCurrPost] = useState({fileName: "", fileType: "", postByte: null});
    const [mediaType, setMediaType] = useState(null);
    const [postLiked, setPostLiked] = useState(false);
    const [show, setShow] = useState(false);

    const companyName = post.page.compName;
    const createdOn = post.createdOn;
    const postDesc = post.description;

    function handleClose(){ setShow(false); }
    function handleShow(){ setShow(true); }

    useEffect(() => {
        requestToPath(`/api/pageLikes/pagePost/${myPostId}/isLiked`, "GET", user.jwt)
            .then((likeResponse) => { setPostLiked(true); })
            .catch((error) => { console.log(error); })
    }, [])

    useEffect(() => {
        // /api/pages/${pageId}/posts/${myPostId}
        requestToPath(`/api/pagePosts/page/${pageId}/posts/${myPostId}`, "GET", user.jwt)
            .then((photoResponse) => {
                setCurrPost(photoResponse);
                setMediaType(photoResponse.fileType.split("/")[0]);    
            })
            .catch((error) => {console.log(error);})
    },[])

    useEffect(() => {
        dayjs.extend(relativeTime);
        const tempTime = dayjs(createdOn).fromNow();
        setTimeCollapsed(tempTime);
    }, [createdOn])

    function displayPostContent(){
        if(mediaType == 'image' && currPost.path != null){
            return <PageImage pageId={pageId} myPostId={myPostId}/>
        }
        else if(mediaType == 'video' && currPost.path != null){
            return <PageVideo pageId={pageId} myPostId={myPostId}/>
        }
    }

    function increaseLike(){
        setPostLiked(true);
        requestToPath(`/api/pageLikes/pagePost/${myPostId}/increaseLike`, "POST", user.jwt)
            .then((likeResponse) => {setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }

    function decreaseLike(){
        setPostLiked(false);
        requestToPath(`/api/pageLikes/pagePost/${myPostId}/decreaseLike`, "DELETE", user.jwt)
            .then((likeResponse) => { setPostLiked(false); })
            .catch((error) => { console.log(error); setPostLiked(true); })
    }

    function displayLikeBtn(){
        if(postLiked){
            return <Button className='flex-fill me-1' onClick={() => { decreaseLike(); }}>Like</Button>
        }
        return <Button className='flex-fill me-1' variant='secondary' onClick={() => { increaseLike(); }}>Like</Button>
    }

    function displayCommentBtn(){
        return <Button className='flex-fill ms-1' variant='secondary' onClick={() => { handleShow(); }}>Comments</Button>
    }

    return (
        <div key={post.id} className="mt-5" style={{display:"flex", flexDirection:"column", justifyContent:"center", alignItems:"center"}}>
            <Card className='custom-postCard'>  
                <Card.Body>
                    <div className='d-flex justify-content-between'>
                        <div>
                            <Card.Title>{companyName}</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">{timeCollapsed}</Card.Subtitle>
                         </div>
                    </div>
                    <Card.Text>{postDesc}</Card.Text>
                </Card.Body>
                {displayPostContent()}

                <Card.Footer className='d-flex'>
                    {displayLikeBtn()}
                    {displayCommentBtn()}
                </Card.Footer>

                <PPostCommentModal compName={post.page.compName} pageO={post.page.user.id} pagePostId={myPostId} show={show} emitHandleClose={handleClose} />                
            </Card>
        </div>
    );
};

export default UserViewPagePost;