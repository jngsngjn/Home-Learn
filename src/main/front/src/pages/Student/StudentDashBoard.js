import React, { useState } from "react";
import "./StudentMain.css";
import RandomVideo from "../../components/Lectures/RandomVideo";
import LectureVideo from "../../components/Lectures/LectureVideo";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import useGetFetch from "../../hooks/useGetFetch";
import { useNavigate } from "react-router-dom";
import { CircularProgressbar, buildStyles } from "react-circular-progressbar";

const StudentDashBoard = () => {
  const navigate = useNavigate();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    file: null,
  });
  const [selectedFileName, setSelectedFileName] = useState("");

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "file") {
      setFormData((prevState) => ({ ...prevState, [name]: files[0] }));
    } else {
      setFormData((prevState) => ({ ...prevState, [name]: value }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(formData);
    closeModal();
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFileName(file.name);
      handleChange(e);
    }
  };

  const {
    data: recentLecture,
    loading: recentLectureLoading,
    error: recentLectureError,
  } = useGetFetch("/data/student/mainpage/recentLecture.json", "");

  const {
    data: question,
    loading: questionLoading,
    error: questionError,
  } = useGetFetch("/data/student/mainpage/question.json", []);

  const {
    data: subject,
    loading: subjectLoading,
    error: subjectError,
  } = useGetFetch("/data/student/mainpage/assignment.json", []);

  const {
    data: badge,
    loading: badgeLoading,
    error: badgeError,
  } = useGetFetch("/data/student/mainpage/badge.json", []);

  const {
    data: adminNotice,
    loading: adminNoticeLoading,
    error: adminNoticeError,
  } = useGetFetch("/data/student/mainpage/adminNotice.json", []);

  const {
    data: teacherNotice,
    loading: teacherNoticeLoading,
    error: teacherNoticeError,
  } = useGetFetch("/data/student/mainpage/teacherNotice.json", []);

  if (
    recentLectureLoading ||
    questionLoading ||
    subjectLoading ||
    badgeLoading ||
    adminNoticeLoading ||
    teacherNoticeLoading
  ) {
    return <div>Loading...</div>;
  }

  if (
    recentLectureError ||
    questionError ||
    subjectError ||
    badgeError ||
    adminNoticeError ||
    teacherNoticeError
  ) {
    return <div>Error loading data</div>;
  }

  return (
    <div className="contents">
      <div className="dashboard_main_container">
        <h1 className="page_title">대시보드</h1>
        <div className="divide_right_container">
          <div className="left_container">
            <div className="recent_lecture_container">
              <div className="title_box">
                <h3 className="components_title">최근 학습 강의</h3>
                <span
                  className="go_to_lecture_page navigate_button"
                  onClick={() =>
                    navigate(`/students/${recentLecture.subject}/board`)
                  }
                >
                  학습 목록 ⟩
                </span>
              </div>
              <div
                className="recent_contents_box"
                onClick={() =>
                  navigate(
                    `/students/${recentLecture.subject}/lectures/${recentLecture.id}`
                  )
                }
              >
                <h3 className="recent_lecture_type">{recentLecture.subject}</h3>
                <div className="recent_video_box">
                  <i className="bi bi-play-btn play_recent_video_icon"></i>
                  <p className="recent_lecture_video_title">
                    {recentLecture.title}
                  </p>
                  <div className="recent_lecture_progress_container">
                    <CircularProgressbar
                      value={recentLecture.progress}
                      styles={buildStyles({
                        pathColor: "#A7D7C5",
                        textColor: "#5C8D89",
                        trailColor: "#d6d6d6",
                      })}
                    />
                    <p className="recent_lecture_percentage">
                      {recentLecture.progress}%
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div className="video_container">
              <h3 className="components_title">오늘의 IT</h3>
              <div className="random_video_box">
                <RandomVideo width="250" height="240" />
              </div>
              <h3 className="components_title">보충 강의</h3>
              <div className="lecture_video_box">
                <LectureVideo width="250" height="240" />
              </div>
            </div>
            <div className="question_container">
              <div className="title_box">
                <h3 className="components_title">질문사항</h3>
                <span className="go_to_inquiry_page navigate_button">
                  더보기 ⟩
                </span>
              </div>
              <div className="question_list_container">
                {question?.map((el, idx) => (
                  <div
                    className="question_list"
                    key={idx}
                    onClick={() =>
                      navigate(`/students/inquiryBoardDetail/${el.id}`)
                    }
                  >
                    <div className="question_type_box">
                      <span className="question_type_tag">질문</span>
                      <span className="question_type_nage">{el.type}</span>
                      <span className="recomment_button">답글 달기 ⟩</span>
                    </div>
                    <div className="question_box">
                      <div className="qusetion_subject_name">
                        {el.lectureName}
                      </div>
                      <p className="student_question_title">
                        {el.questionTitle}
                      </p>
                      <div className="question_icon_box">
                        <i className="bi bi-eye watch_icon"></i>
                        {el.watchCount}
                        <i className="bi bi-chat comment_icon"></i>
                        {el.commentCount}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            {/* 배지 컨테이너 */}
            <div className="badge_container">
              <div className="title_box">
                <h3 className="components_title">배지</h3>
                <span className="go_to_badge_page navigate_button">
                  더보기 ⟩
                </span>
              </div>
              <div className="badge_list_box">
                {badge.map((el, idx) => (
                  <div className="badge_list" key={idx}>
                    <img
                      src={el.image_path}
                      alt="badge"
                      className="badge_image"
                    />
                  </div>
                ))}
              </div>
            </div>
          </div>
          {/* 오른쪽 메인 부분 */}
          <div className="right_container">
            <div className="calander-container">
              <h3 className="components_title">캘린더</h3>
              <div className="calander"> {/* 캘린더 컴포넌트 */}</div>
            </div>
            <div className="subject_container">
              <div className="title_box">
                <h3 className="components_title"> 과제 목록</h3>
                <span
                  className="go_to_subject_page navigate_button"
                  onClick={() => navigate("/students/assignment")}
                >
                  더보기
                </span>
              </div>
              <div className="dashboard_subject_list_container">
                {subject.map((el, idx) => (
                  <div className="dashboard_subject_list_box" key={idx}>
                    <h3>과제</h3>
                    <h4 className="subject_name">{el.title}</h4>
                    <p className="subject_description">{el.content}</p>
                    <button
                      className="subject_submit_button"
                      onClick={openModal}
                    >
                      제출하기
                    </button>
                    {/* subject response의 timeline이 뭔지 모르겠음 물어보기 */}
                    {/* <div className="show_subject_complete">
                      {subject.timeout.some((el) => el.status === "true")
                        ? "제출 여부 ✅"
                        : "제출 여부 ❌"}
                    </div> */}
                    <div className="addtional_info_box">
                      <span className="go_to_subject_page">상세보기 ⟩</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            <div className="notice_container">
              <div className="admin_notice_container">
                <h3 className="notice_components_title">관리자 공지사항</h3>
                {adminNotice.map((el, idx) => (
                  <div className="notice_list" key={idx}>
                    <div className={`notice_type ${el.type}_notice`}>
                      {el.type === "alert" ? "긴급" : "공지"}
                    </div>
                    <div className="notice_title">{el.title}</div>
                    <span className="notice_date">{el.writeDate}</span>
                  </div>
                ))}
                <span className="go_to_admin_notice_page navigate_button">
                  자세히 보기 ⟩
                </span>
              </div>

              <div className="admin_notice_container">
                <h3 className="notice_components_title">선생님 공지사항</h3>
                {teacherNotice.map((el, idx) => (
                  <div key={idx} className="notice_list">
                    <div className={`notice_type ${el.type}_notice`}>
                      {el.type === "alert" ? "긴급" : "공지"}
                    </div>
                    <div className="notice_title">{el.title}</div>
                    <span className="notice_date">{el.writeDate}</span>
                  </div>
                ))}
                <div className="r">
                  <span className="go_to_admin_notice_page">자세히 보기 ⟩</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      {isModalOpen && (
        <div className="modal show">
          <div className="modal-content-student" style={{ width: "700px" }}>
            <span className="close" onClick={closeModal}>
              &times;
            </span>
            <h1 className="modal_title">과제 제출</h1>
            <form onSubmit={handleSubmit} className="modal_form_body">
              <label>
                <p className="modal_name_tag">제목</p>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  className="modal_input_title"
                />
              </label>
              <label>
                <p className="modal_content_tag">내용</p>
                <textarea
                  name="content"
                  value={formData.content}
                  onChange={handleChange}
                  className="modal_input_content"
                ></textarea>
              </label>
              <label className="modal_file_label">
                <p className="modal_file_tag">파일 첨부</p>
                <div className="modal_file_input_wrapper">
                  <input
                    type="text"
                    readOnly
                    value={selectedFileName}
                    className="modal_input_file_display"
                  />
                  <label className="modal_file_button">
                    파일 선택
                    <input
                      type="file"
                      name="file"
                      onChange={handleFileChange}
                      className="modal_input_file"
                    />
                  </label>
                </div>
              </label>
              <div className="modal_submit_button_box">
                <button type="submit" className="modal_submit_button">
                  과제 제출
                </button>
                <button
                  type="button"
                  className="modal_cancel_button"
                  onClick={closeModal}
                >
                  제출 취소
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentDashBoard;