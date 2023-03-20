import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';
import PageImage from '../PageMedia/PageImage';
import PageVideo from '../PageMedia/PageVideo';
import { useNavigate } from 'react-router-dom';
import PPostCommentModal from '../../Modals/Pages/Comments/PPostCommentModal';

const ViewDashPostsPage = (props) => {
    const user = useUser();
    const {currPost, loggedUser} = props;
    const navigate = useNavigate();

    const [mediaType, setMediaType] = useState(null);
    const [currMedia, setCurrMedia] = useState(null);
    const [timeCollapsed, setTimeCollapsed] = useState(null);
    const [postLiked, setPostLiked] = useState(false);
    const [show, setShow] = useState(false);

    const pageId = currPost.page.id;
    const myPostId = currPost.id;

    const companyName = currPost.page.compName;
    const createdOn = currPost.createdOn;
    const postDesc = currPost.description;

    function handleClose(){ setShow(false); }
    function handleShow(){ setShow(true); }

    useEffect(() => {
        // /api/pages/${pageId}/posts/${myPostId}
        requestToPath(`/api/pagePosts/page/${pageId}/posts/${myPostId}`, "GET", user.jwt)
            .then((photoResponse) => {
                setCurrMedia(photoResponse);
                setMediaType(photoResponse.fileType.split("/")[0]);    
            })
            .catch((error) => { console.log(error); })
    },[])

    useEffect(() => {
        dayjs.extend(relativeTime);
        const tempTime = dayjs(createdOn).fromNow();
        setTimeCollapsed(tempTime);
    }, [createdOn])

    useEffect(() => {
        requestToPath(`/api/pageLikes/pagePost/${myPostId}/isLiked`, "GET", user.jwt)
            .then((likeResponse) => { setPostLiked(true); })
            .catch((error) => { console.log(error); })
    }, [])
  
    function displayMedia(){
        if(mediaType != null && currMedia.path != null){
            if(mediaType != 'video'){
                return <PageImage pageId = {pageId} myPostId = {myPostId} />
            }
            return <PageVideo pageId = {pageId} myPostId = {myPostId} />
        }
        return;
    }

    function handleClick(){ navigate(`/users/${loggedUser}/pages/page/${pageId}`) }
    function handleVisitBtn(){ return <Button onClick={handleClick}>View</Button> }

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
        <div className="mt-5 d-flex flex-column justify-content-center align-items-center">
            <Card className='custom-postCard'>  
                <Card.Body>
                    <div className='d-flex justify-content-between'>
                        <div>
                            <Card.Title>{companyName}</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">{timeCollapsed}</Card.Subtitle>
                        </div>
                        <div>
                            {handleVisitBtn()}
                        </div>
                    </div>
                    <Card.Text>
                        {postDesc}
                    </Card.Text>
                </Card.Body>
                    {displayMedia()}
                <Card.Footer className='d-flex'>
                    {displayLikeBtn()}
                    {displayCommentBtn()}
                </Card.Footer>
                <PPostCommentModal compName={currPost.page.compName} pageO={currPost.page.user.id} pagePostId={myPostId} show={show} emitHandleClose={handleClose} />
            </Card>  
        </div>
    );
};

export default ViewDashPostsPage;